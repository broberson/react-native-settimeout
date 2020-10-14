@import UIKit;
#import "RNSetTimeout.h"

@implementation RNSetTimeout {
    UIBackgroundTaskIdentifier bgTask;
    int delay;
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents { return @[@"RNSetTimeout", @"RNSetTimeout.timeout"]; }

RCT_EXPORT_METHOD(setTimeout:(int)timeoutId
                     timeout:(int)timeout
                    resolver:(RCTPromiseResolveBlock)resolve
                    rejecter:(RCTPromiseRejectBlock)reject)
{

    NSLog( @"SetTimeout called" );

    __block UIBackgroundTaskIdentifier task = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"RNSetTimeout" expirationHandler:^{
        [[UIApplication sharedApplication] endBackgroundTask:task];
        task = UIBackgroundTaskInvalid;
    }];

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeout * NSEC_PER_MSEC), dispatch_get_main_queue(), ^{
        if ([self bridge] != nil) {
            [self sendEventWithName:@"RNSetTimeout.timeout" body:[NSNumber numberWithInt:timeoutId]];
        }
        [[UIApplication sharedApplication] endBackgroundTask:task];
        task = UIBackgroundTaskInvalid;
    });

    resolve([NSNumber numberWithBool:YES]);
}


RCT_EXPORT_METHOD(clearTimeout:(int)timeoutId
                      resolver:(RCTPromiseResolveBlock)resolve
                      rejecter:(RCTPromiseRejectBlock)reject)
{
    // nothing to do, javascript side will ignore this event firing
}

@end

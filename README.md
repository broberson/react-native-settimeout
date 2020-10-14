# IMPORTANT: This is meant for use in an app that plays background audio.

On iOS/iPadOS background tasks longer than 30 seconds are killed by the operating system. The specific use-case for this native module is to stop downloading streaming audio when the audio has been paused for more than a couple of minutes.

Because the app in question plays background audio, and that audio is paused when these background tasks are created, iOS allows them to run (on iOS 13 -
we'll have to see what iOS 14+ does with this.) If you're not doing something similar, this probably won't work for you on
iOS.

On Android devices, everything seems to work fine.

## Getting started

`$ yarn add broberson/react-native-settimeout`

`$ cd ios && pod install && cd ..`

### Mostly automatic installation

`$ react-native link react-native-settimeout`

## Usage

```typescript
import RNSetTimeout from 'react-native-settimeout';

export const Thing = () => {
  useEffect(() => {
    const handle: number = RNSetTimeout.setTimeout(() => {
      console.log('This should run 3 minutes later.');
    }, 3 * 60 * 1000);

    return () => RNSetTimeout.clearTimeout(handle);
  }, []);

  return <View />;
};
```

## Issues

I won't be addressing issues, don't bother reporting them. Feel free to fork the repo and do what you want with it.

## License

This code is MIT licensed.

## Origin

Most of this code was lifted from [@ocetnik/react-native-background-timer](https://github.com/ocetnik/react-native-background-timer) which tries to do much more than I needed it to do. This is my attempt to trim it down to the essentials and make it work for the specific use-case I was addressing.

Please credit Dávid Ocetník, and the other kind people that contributed to his repository, for anything good that happens to come from this. Feel free to blame me for whatever bugs you might encounter.

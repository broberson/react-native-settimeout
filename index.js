import { NativeModules, NativeEventEmitter } from 'react-native';

const { RNSetTimeout } = NativeModules;
const Emitter = new NativeEventEmitter(RNSetTimeout);

class SetTimeout {
  id = 0;
  callbacks = {};

  constructor() {
    Emitter.addListener('RNSetTimeout.timeout', (id) => {
      if (this.callbacks[id]) {
        const callback = this.callbacks[id].callback;

        if (!this.callbacks[id].interval) {
          delete this.callbacks[id];
        } else {
          RNSetTimeout.setTimeout(id, this.callbacks[id].timeout);
        }

        callback();
      }
    });
  }

  setTimeout = (callback, timeout) => {
    const id = ++this.id;
    this.callbacks[id] = {
      callback: callback,
      interval: false,
      timeout: timeout,
    };
    RNSetTimeout.setTimeout(id, timeout);
    return id;
  };

  clearTimeout = (id) => {
    if (this.callbacks[id]) {
      delete this.callbacks[id];
      RNSetTimeout.clearTimeout(id);
    }
  };
}

export default new SetTimeout();

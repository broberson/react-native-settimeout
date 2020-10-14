declare module 'react-native-settimeout' {
  function setTimeout(callback: Function, timeout: number): number;
  function clearTimeout(id: number): void;
}

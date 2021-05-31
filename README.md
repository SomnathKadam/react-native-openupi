# react-native-openupi

Deep linking UPI for react-native in Android

## Installation

```sh
npm install react-native-openupi
```

or using yarn

```sh
yarn add react-native-openupi
```

## Usage

```js
import Openupi from 'react-native-openupi';

// ...

//replace below param as per your requirement

Openupi.StartPayment(
  'upi://pay?pa=[pa]&pn=[pn]&&tr=[orderID]&am=[amount]',
  orderID
).then((data) => {
  //resolve
  console.log(data);

  console.log(data.status , data.orderID + ': ' + data.message);

  return;
});
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

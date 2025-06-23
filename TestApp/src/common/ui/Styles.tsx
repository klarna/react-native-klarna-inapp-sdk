import { StyleSheet } from 'react-native';

export function backgroundStyle(style: any, isDarkMode: boolean) {
  return [style, background(isDarkMode)];
}

export function background(isDarkMode: boolean) {
  return {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };
}

export const Colors = {
  white: '#FFF',
  lighter: '#F3F3F3',
  light: '#DAE1E7',
  lightGray: '#d2d2d2',
  dark: '#444',
  darker: '#222',
  black: '#000',
  pink: '#ffc0cb',
};

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
    flexGrow: 1,
  },
  column: {
    display: 'flex',
    flexDirection: 'column',
    flex: 1,
  },
  columnHeader: {
    flexShrink: 0,
  },
  columnFooter: {
    flexShrink: 0,
  },
  columnItemFill: {
    flexGrow: 1,
  },
  tokenInput: {
    flexDirection: 'column',
    justifyContent: 'space-around',
    alignItems: 'center',
    borderColor: 'gray',
    height: 40,
    borderWidth: 1,
    padding: 10,
    margin: 20,
  },
  urlInput: {
    borderColor: 'gray',
    borderWidth: 1,
    flex: 1,
    padding: 10,
  },
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
  },
  paymentContainer: {
    flex: 1,
    flexDirection: 'column',
    flexWrap: 'wrap',
    alignItems: 'center',
    backgroundColor: Colors.pink,
    padding: 10,
    width: '100%',
  },
  paymentView: {
    width: '100%',
    flexGrow: 1,
  },
  title: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  buttonsContainer: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    margin: 10,
  },
  button: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 7,
    paddingHorizontal: 10,
    borderRadius: 4,
    elevation: 3,
    backgroundColor: Colors.pink,
  },
  buttonText: {
    textAlign: 'center',
    color: Colors.white,
  },
  navMenuItem: {
    fontSize: 20,
    textAlign: 'center',
    margin: 20,
  },
  componentView: {
    width: '100%',
    flexGrow: 1,
  },
  signInTextFieldStyle: {
    marginBottom: 20,
    width: '80%',
  },
});

export default styles;

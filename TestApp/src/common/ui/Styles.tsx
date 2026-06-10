import { StyleSheet, useColorScheme } from 'react-native';

export function foregroundColor(isDarkMode: boolean) {
  return isDarkMode ? Colors.white : '#333333';
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

export type Theme = {
  text: string;
  placeholder: string | undefined;
  background: string;
  card: string;
  surface: string;
  optionItem: string;
  buttonText: string;
};

const lightTheme: Theme = {
  text: '#333333',
  placeholder: undefined,
  background: Colors.lighter,
  card: Colors.lighter,
  surface: Colors.white,
  optionItem: Colors.light,
  buttonText: Colors.white,
};

const darkTheme: Theme = {
  text: Colors.white,
  placeholder: Colors.lightGray,
  background: Colors.darker,
  card: Colors.dark,
  surface: Colors.black,
  optionItem: Colors.dark,
  buttonText: Colors.black,
};

export function useTheme(): Theme {
  return useColorScheme() === 'dark' ? darkTheme : lightTheme;
}

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
  screenContent: {
    padding: 10,
  },
  switchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginVertical: 6,
    paddingHorizontal: 10,
  },
  card: {
    marginVertical: 10,
    padding: 10,
    borderRadius: 6,
  },
});

export default styles;

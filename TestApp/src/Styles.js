import {StyleSheet} from 'react-native';

export const backgroundStyle = isDarkMode => {
  return {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };
};

export const Colors = {
  white: '#FFF',
  lighter: '#F3F3F3',
  light: '#DAE1E7',
  dark: '#444',
  darker: '#222',
  black: '#000',
};

const styles = StyleSheet.create({
  outer: {
    flex: 1,
    flexGrow: 1,
  },
  scrollView: {
    flex: 1,
    flexGrow: 1,
  },
  scrollViewContentContainer: {
    justifyContent: 'space-between',
  },
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: Colors.light,
    width: '100%',
  },
  header: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
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
  button: {
    height: 10,
    margin: 10,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  navMenuItem: {
    fontSize: 20,
    textAlign: 'center',
    margin: 20,
  },
});

export default styles;

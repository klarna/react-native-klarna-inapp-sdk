import {StyleSheet} from 'react-native';

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
    backgroundColor: '#F5FCFF',
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
    marginLeft: 20,
    marginRight: 20,
    marginBottom: 10,
  },
  button: {
    height: 10,
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
});

export default styles;

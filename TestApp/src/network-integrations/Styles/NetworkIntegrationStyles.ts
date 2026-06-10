import { StyleSheet } from 'react-native';
import { Colors } from '../../common/ui/Styles';

const networkIntegrationStyles = StyleSheet.create({
  container: {
    padding: 20,
  },
  buttonSpacing: {
    marginTop: 8,
  },
  fieldLabel: {
    fontWeight: 'bold',
    textAlign: 'left',
    marginBottom: 6,
  },
  logLabel: {
    marginTop: 12,
    fontSize: 14,
  },
  logContainer: {
    marginTop: 8,
    borderWidth: 1,
    borderColor: Colors.lightGray,
    borderRadius: 4,
    minHeight: 400,
    padding: 8,
  },
  logText: {
    fontSize: 12,
  },
  fieldContainer: {
    marginBottom: 8,
  },
  sectionTitle: {
    fontWeight: 'bold',
    fontSize: 16,
    marginTop: 16,
    marginBottom: 8,
  },
  previewContainer: {
    borderRadius: 6,
    overflow: 'hidden',
  },
  hint: {
    color: '#999',
    textAlign: 'center',
    marginVertical: 12,
  },
  errorContainer: {
    marginTop: 12,
    padding: 10,
    backgroundColor: '#ffe0e0',
    borderRadius: 6,
  },
  errorTitle: {
    fontWeight: 'bold',
    color: '#c00',
    marginBottom: 4,
  },
  errorText: {
    color: '#600',
    fontSize: 13,
  },
  infoContainer: {
    marginTop: 20,
    padding: 10,
    backgroundColor: '#e8f4e8',
    borderRadius: 6,
  },
  infoTitle: {
    fontWeight: 'bold',
    color: '#060',
    marginBottom: 4,
  },
  infoText: {
    color: '#040',
    fontSize: 13,
  },
  loadingIndicator: {
    marginTop: 12,
  },
});

export default networkIntegrationStyles;

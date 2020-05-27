/* eslint-disable react-native/no-inline-styles */
import React, {useState, useEffect} from 'react';
import {
  SafeAreaView,
  View,
  Text,
  TextInput,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import firestore from '@react-native-firebase/firestore';
import AsyncStorage from '@react-native-community/async-storage';
import Snackbar from 'react-native-snackbar';

const Signup = props => {
  const [isLoading, setIsLoading] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [error, seterror] = useState('');
  const {container, borderContainer} = styles;

  // useEffect(() => {
  //   async function getData() {
  //     try {
  //       const value = await AsyncStorage.getItem('user');
  //       if (value !== null) {
  //         props.navigation.navigate('Home', {
  //           name: JSON.parse(value)['name'],
  //           email: JSON.parse(value)['email'],
  //         });
  //         // value previously s
  //       }
  //     } catch (e) {
  //       // error reading value
  //     }
  //   }
  //   getData();
  //   return () => {};
  // }, [props.navigation]);

  const onSIgninPressed = async () => {
    setIsLoading(true);

    // when submit is pressed add the email, first name and password to the collection
    const documentSnapshot = await firestore()
      .collection('users')
      .add({
        name,
        email,
        password,
      });

    // After adding to the collection navigate to login screen for the user to login
    if (documentSnapshot) {
      Snackbar.show({
        text: 'Account created successfully. Log In',
        duration: Snackbar.LENGTH_SHORT,
      });
      props.navigation.navigate('login');
    } else {
      seterror('No Account exists with the email');
    }
    // setPasswoinrd('No registered email');
    setIsLoading(false);
  };

  return (
    <SafeAreaView style={container}>
      <Text
        style={{
          fontWeight: 'bold',
          fontSize: 32,
          textAlign: 'center',
          marginVertical: 32,
          fontStyle: 'italic',
          color: '#410DAA',
        }}>
        ParkIT
      </Text>
      <Text>{error}</Text>
      <Text
        style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
        Name
      </Text>
      {/* Input for name */}
      <View style={borderContainer}>
        <TextInput value={name} onChangeText={name => setName(name)} />
      </View>
      <Text
        style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
        Email
      </Text>
      {/* Input for email */}
      <View style={borderContainer}>
        <TextInput value={email} onChangeText={email => setEmail(email)} />
      </View>
      <Text
        style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
        Password
      </Text>
      {/* Input for password */}
      <View style={borderContainer}>
        <TextInput
          value={password}
          onChangeText={password => setPassword(password)}
        />
      </View>
      <TouchableOpacity
        onPress={onSIgninPressed}
        style={{
          padding: 16,
          margin: 16,
          backgroundColor: '#410DAA',
          alignItems: 'center',
          borderRadius: 8,
          alignSelf: 'center',
          paddingHorizontal: 32,
        }}>
        {isLoading ? (
          <ActivityIndicator color={'#fff'} />
        ) : (
          <Text style={{color: '#fff'}}>Sign Up</Text>
        )}
      </TouchableOpacity>
      {/* If the user has an acount navigate back to login */}
      <TouchableOpacity onPress={() => props.navigation.goBack()}>
        <Text style={{textAlign: 'center'}}>
          Already have an Account ? &nbsp;
          <Text
            style={{
              textAlign: 'center',
              textDecorationLine: 'underline',
              padding: 8,
              fontSize: 16,
              color: '#410DAA',
            }}>
            Log In
          </Text>
        </Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  borderContainer: {
    borderColor: '#ddd',
    borderBottomWidth: 1,
    borderRadius: 4,
    height: 48,
    justifyContent: 'center',
    marginBottom: 16,
    paddingHorizontal: 16,
    fontSize: 16,
    marginHorizontal: 16,
  },
});

export default Signup;

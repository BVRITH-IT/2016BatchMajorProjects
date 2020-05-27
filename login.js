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

const Login = props => {
  const [isLoading, setIsLoading] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, seterror] = useState('');
  const {container, borderContainer} = styles;

  useEffect(() => {
    async function getData() {
      try {
        const value = await AsyncStorage.getItem('user');
        if (value !== null) {
          props.navigation.navigate('Home', {
            name: JSON.parse(value)['name'],
            email: JSON.parse(value)['email'],
          });
          // value previously s
        }
      } catch (e) {
        // error reading value
      }
    }
    getData();
    return () => {};
  }, [props.navigation]);

  const onLoginPressed = async () => {
    // set loading to true
    setIsLoading(true);

    // Get the list of users from firebase

    const documentSnapshot = await firestore()
      .collection('users')
      .get();
    // await documentSnapshot
    //   .where('email', '==', email)
    //   .limit(1)
    //   .onSnapshot(Loaddata);

    /**
     * Check whether the user entered data is presenet in the table
     *  If yes navigate yo yhe home screen
     * */

    documentSnapshot.forEach(async doc => {
      seterror(doc.data());
      if (doc.data()['email'] == email && doc.data()['password'] == password) {
        // seterror(doc.data());
        await AsyncStorage.setItem('user', JSON.stringify(doc.data()));
        props.navigation.navigate('Home', {
          name: doc.data()['name'],
          email: doc.data()['email'],
        });
      }
    });

    //set loading to false
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
      <Text>{JSON.stringify(error)}</Text>
      <Text
        style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
        Email
      </Text>

      {/* Input for mail */}
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
        onPress={onLoginPressed}
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
          <Text style={{color: '#fff'}}>Login</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => {
          props.navigation.navigate('signup');
        }}>
        <Text style={{textAlign: 'center'}}>
          Don't have an Account ? &nbsp;
          <Text
            style={{
              textAlign: 'center',
              textDecorationLine: 'underline',
              padding: 8,
              fontSize: 16,
              color: '#410DAA',
            }}>
            Sign Up
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

export default Login;

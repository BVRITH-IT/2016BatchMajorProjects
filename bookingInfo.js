/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable react-native/no-inline-styles */
import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
  Picker,
  ActivityIndicator,
} from 'react-native';
import Heading from '../components/heading';
import DateTimePicker from '@react-native-community/datetimepicker';
import moment from 'moment';
import firestore from '@react-native-firebase/firestore';
import Snackbar from 'react-native-snackbar';
import Modal from 'react-native-modal';

const BookingInfo = props => {
  const data = [...Array(20).keys()].map((y, index) => ({
    bookedBy: '',
    for: '',
    from: '',
    bookedAt: '',
    on: '',
    id: props.route.params.spotName + index,
  }));

  const {container, spotName} = styles;
  const [spots, setSpots] = useState([]);
  const {} = props;
  const [date, setDate] = useState(moment().valueOf());
  const [time, setTime] = useState(moment().valueOf());
  const [unformatted, setUnformatted] = useState(
    moment(new Date()).format('h:mm a'),
  );
  const [mode, setMode] = useState('date');
  const [show, setShow] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [isLoading, setIsloading] = useState(false);
  const [message, setMessage] = useState('');
  const [hours, setHours] = useState('1');

  const onChange = value => {
    setShow(false);
    if (mode == 'date') {
      // const formattedDate = moment(value.nativeEvent.timestamp).format(
      //   'MMM DD, YYYY',
      // );
      setDate(value.nativeEvent.timestamp);
    } else {
      const newTime = moment(value.nativeEvent.timestamp).add(1, 'hours');
      setMessage(newTime);
      // const formattedTime = moment(value.nativeEvent.timestamp).format(
      //   'h:mm a',
      // );
      setTime(value.nativeEvent.timestamp);
    }
    setShow(false);
  };

  const getUserData = async () => {
    const documentSnapshot = await firestore()
      .collection('spots')
      .doc(props.route.params.spotName)
      .get();

    setSpots(documentSnapshot.data().spots);
    // setSpots(JSON.parse(documentSnapshot.data().spots));
    // setUsers(documentSnapshot[0].data());
    // documentSnapshot.forEach(doc => {
    //   setSpots([...spots, ...doc.data()]);
    // });
    // setUsers(documentSnapshot.docs);
  };

  useEffect(() => {
    getUserData();
  }, []);

  const saveSpot = async () => {
    setIsloading(true);
    const newDate = new Date();
    const newData = spots.map(spot => {
      if (spot.id == props.route.params.spotId) {
        return {
          ...spot,
          bookedBy: props.route.params.email,
          for: hours,
          from: time,
          on: date,
          bookedAt: newDate,
        };
      }
      return spot;
    });
    await firestore()
      .collection('bookings')
      .add({
        bookedBy: props.route.params.email,
        for: hours,
        from: time,
        on: date,
        bookedAt: newDate,
        spot: props.route.params.spot,
        spotName: props.route.params.spotName,
        spotId: props.route.params.spotId,
        status: 'booked',
      });
    await firestore()
      .collection('spots')
      .doc(props.route.params.spotName)
      .update({
        spots: newData,
      })
      .then(function() {
        setIsloading(false);
        setShowModal(false);
        setMessage('saved successfully');
        Snackbar.show({
          text: 'Booked Successfully',
          duration: Snackbar.LENGTH_SHORT,
        });
        props.navigation.goBack();
        console.log('Document successfully updated!');
      })
      .catch(function(error) {
        // The document probably doesn't exist.
        setIsloading(false);
        setMessage('error' + error);
        setShowModal(false);
        console.error('Error updating document: ', error);
      });
    setIsloading(false);
  };

  return (
    <SafeAreaView style={container}>
      <ScrollView style={{flex: 1}}>
        <Heading>{props.route.params.spot}</Heading>
        <Text>{JSON.stringify(props.route.params)}</Text>
        {/* <Text>{moment(message).format('h:mm a')}</Text> */}
        <Text
          style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
          Date
        </Text>
        <View style={styles.borderContainer}>
          <Text
            onPress={() => {
              setShow(true);
              setMode('date');
            }}>
            {moment(date).format('MMM DD, YYYY')}
          </Text>
        </View>
        <Text style={{marginHorizontal: 16, fontWeight: 'bold'}}>Time</Text>
        <View style={styles.borderContainer}>
          <Text
            onPress={() => {
              setShow(true);
              setMode('time');
            }}>
            {moment(time).format('h:mm a')}
          </Text>
        </View>

        <Text
          style={{marginHorizontal: 16, marginBottom: 16, fontWeight: 'bold'}}>
          How many hours ?{' '}
        </Text>
        <View style={styles.borderContainer}>
          <Picker onValueChange={item => setHours(item)} selectedValue={hours}>
            <Picker.Item label="1hrs" value="1" />
            <Picker.Item label="2hrs" value="2" />
            <Picker.Item label="3hrs" value="3" />
            <Picker.Item label="4hrs" value="4" />
          </Picker>
        </View>

        {show && (
          <DateTimePicker
            testID="dateTimePicker"
            timeZoneOffsetInMinutes={0}
            mode={mode}
            is24Hour={true}
            display="default"
            onChange={onChange}
            value={new Date()}
          />
        )}
      </ScrollView>
      <TouchableOpacity
        style={{
          padding: 16,
          margin: 16,
          backgroundColor: '#410DAA',
          alignItems: 'center',
          borderRadius: 8,
        }}
        onPress={() => {
          setShowModal(true);
        }}>
        <Text style={{color: '#fff'}}>Book</Text>
      </TouchableOpacity>
      <Modal
        isVisible={showModal}
        onBackdropPress={() => {
          setShowModal(false);
        }}
        style={{justifyContent: 'center', alignItems: 'center', flex: 1}}>
        <View
          style={{
            backgroundColor: '#fff',
            padding: 16,
            borderRadius: 8,
            width: '70%',
          }}>
          <Text style={{textAlign: 'center'}}>You have to pay</Text>
          <Text
            style={{
              textAlign: 'center',
              fontSize: 32,
              fontWeight: 'bold',
              fontStyle: 'italic',
              color: '#410DAA',
            }}>
            &#8377; {props.route.params.price * Number(hours)}
          </Text>
          <Text style={{textAlign: 'center'}}>when you check-in.</Text>

          <TouchableOpacity
            style={{
              padding: 16,
              margin: 16,
              backgroundColor: '#410DAA',
              alignItems: 'center',
              borderRadius: 8,
            }}
            onPress={saveSpot}>
            {isLoading ? (
              <ActivityIndicator color={'#fff'} />
            ) : (
              <Text style={{color: '#fff'}}>Book Now</Text>
            )}
          </TouchableOpacity>
        </View>
      </Modal>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    flex: 1,
  },

  spotName: {
    fontSize: 20,
    marginHorizontal: 32,
    marginBottom: 16,
  },
  borderContainer: {
    borderColor: '#ddd',
    borderWidth: 1,
    borderRadius: 4,
    height: 48,
    justifyContent: 'center',
    marginBottom: 16,
    paddingHorizontal: 16,
    fontSize: 16,
    marginHorizontal: 16,
  },
});

export default BookingInfo;

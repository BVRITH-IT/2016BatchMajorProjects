import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

const Heading = props => {
  const {container, headingText, sideHeading} = styles;
  return (
    <View style={container}>
      <View style={sideHeading} />
      <Text style={headingText}>{props.children}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    marginVertical: 16,
  },
  headingText: {
    fontWeight: 'bold',
    fontSize: 18,
  },
  sideHeading: {
    width: 20,
    height: 24,
    backgroundColor: '#410DAA',
    borderTopRightRadius: 8,
    borderBottomRightRadius: 8,
    marginRight: 16,
  },
});

export default Heading;

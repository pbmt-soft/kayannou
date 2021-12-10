/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';

import {SafeAreaView, StatusBar, useColorScheme} from 'react-native';

import HomeScreen from './src/screens/home';
import Post from './src/components/post';

const App: () => React$Node = () => {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <SafeAreaView>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      {/* <HomeScreen /> */}
      <Post />
      <Post />
      <Post />
    </SafeAreaView>
  );
};

export default App;

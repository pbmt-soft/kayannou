import React from 'react';
import {View, ImageBackground, Text, Pressable} from 'react-native';
import styles from './styles.js';
import Fontisto from 'react-native-vector-icons/Fontisto';

const HomeScreen = props => {
  return ( 
    <View>
     
      <ImageBackground
        source={require('../../../assets/images/7.jpg')}
        style={styles.image}>

        <Text style={styles.title}>Go Near</Text>

        <Pressable style={styles.searchButton}
        onPress={() => {console.warn( 'Search btn click');
    }
}
        >
            <Fontisto name="search" size={25} color={"#f15454"}/>
           <Text style={styles.buttonTextSearch}> Where are you going? </Text>

        </Pressable>

        <Pressable style={styles.button}
        onPress={() => {console.warn( 'Explore btn click');
    }
}
        >
           <Text style={styles.buttonText}> Explore nearby stays</Text>

        </Pressable>

      </ImageBackground>

    </View>
  );
};

export default HomeScreen;

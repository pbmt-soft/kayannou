import {StyleSheet,Dimensions} from 'react-native';

const styles = StyleSheet.create({
  images: {
    width: '100%',
    height: 500,
    resizeMode: 'cover',
    justifyContent: 'center',
  },
  title: {
    fontSize: 135,
    fontWeight: 'normal',
    color: 'white',
    width: '80%',
    marginTop:70,
    marginLeft: 25,
  },
  button:{
      backgroundColor:'#fff',
      width:250,
      height:55,
      marginLeft:25,
      marginTop:25,
      marginBottom:10,
      borderRadius:10,
      justifyContent:'center',
      alignItems:'center',
  },
  buttonText:{
      fontSize:16,
      fontWeight:'bold',
  },
  searchButton:{
    backgroundColor:'#fff',
    height:60,
    width:Dimensions.get('window').width - 20,
    flexDirection:'row',
    marginHorizontal:10,  
    borderRadius:30,
    justifyContent:'center',
    alignItems:'center',
    position:'absolute',
    top:50,
    zIndex:100,

  },
  buttonTextSearch:{
    fontSize:16,
    fontWeight:'bold',
  },
});

export default styles;

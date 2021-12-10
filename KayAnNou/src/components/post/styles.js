import { StyleSheet } from "react-native";

const styles=StyleSheet.create({
    container:{
        margin:20,
    },
image:{
    width:'100%',
    aspectRatio:3 / 2,
    resizeMode:'cover',
    borderRadius:10,

},
bedrooms:{
    marginVertical:10,
    color:'#5b5b5b',

},
description:{
    fontSize:18,
    lineHeight:26,
},
prices:{
    fontSize:18,
    color:'red',
    marginVertical:5,
},
oldprice:{
    color:'grey',
    textDecorationLine:'line-through',
    marginRight:10,
    fontWeight:"bold",
},
newprice:{
    fontWeight:'bold', 
    color:'black',
},
totalprices:{
    fontSize:18,
    fontWeight:'bold',
    textDecorationLine:'underline',
    color:'blue',
},
});

export default styles;
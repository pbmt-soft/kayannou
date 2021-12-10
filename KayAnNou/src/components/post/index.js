import React from "react";
import {View,Text,Image} from "react-native";

import styles from './styles.js';

const Post = (props) => {
    return(
        <View style={styles.container}>

             {/*Image */}
            <Image style={styles.image} source={{uri:'https://th.bing.com/th/id/R.5861fe409585d05474b190f5f9175dc6?rik=18tLXgLbbQM6VA&pid=ImgRaw&r=0'}} />

            {/*Bed and bedroom */}
            <Text style={styles.bedrooms} >1 bed . 1 bedroom</Text>

            {/*Type and description*/}
            <Text style={styles.description} numberOfLines={2} >Entire flat.Port-au-Prince,
                Lorem ipsum dolor sit amet consectetur adipisicing elit. 
                Ducimus dolor necessitatibus quia non sapiente, obcaecati magnam
                facilis nihil similique? Omnis asperiores magni quis dolorum 
                inventore est consequuntur quasi quae placeat?</Text>

            {/*old price and new price */}

            <Text style={styles.prices} >
                <Text style={styles.oldprice}>$45 </Text>
                
                <Text style={styles.newprice}> $35 </Text>
                 / night
            </Text>


            {/*total price */}

            <Text style={styles.totalprices} >$230 Total</Text>

        </View>
    );
};

export default Post;
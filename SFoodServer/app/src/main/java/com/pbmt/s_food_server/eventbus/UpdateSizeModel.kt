package com.pbmt.s_food_server.eventbus

import com.pbmt.s_food_server.model.SizeModel

class UpdateSizeModel {
    var sizeModelList:List<SizeModel> ? =null
    constructor()

    constructor(sizeModelList:List<SizeModel>?){
        this.sizeModelList=sizeModelList
    }

}
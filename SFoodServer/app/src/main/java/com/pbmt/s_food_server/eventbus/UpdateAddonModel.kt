package com.pbmt.s_food_server.eventbus

import com.pbmt.s_food_server.model.AddonModel


class UpdateAddonModel {

    var addonModelList:List<AddonModel> ? =null
    constructor()

    constructor(addonModelList:List<AddonModel>?){
        this.addonModelList=addonModelList
    }
}
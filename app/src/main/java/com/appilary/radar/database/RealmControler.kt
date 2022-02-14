package com.appilary.radar.database

import com.appilary.radar.api.body.SurveyPostReqBody
import com.appilary.radar.api.res.CreateSurveyRes
import com.appilary.radar.api.res.SurveyAppConfigData
import com.appilary.radar.api.res.SurveyAppFormData
import com.appilary.radar.database.survey.SurveyFileRealm
import com.appilary.radar.database.survey.SurveySingleResData
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File

/**
 * Created by vi.garg on 3/2/18.
 */
object RealmControler {

    init {
        val config = RealmConfiguration.Builder()
//                .schemaVersion() // Must be bumped when the schema changes
//                .migration(CustomMigration()) // Migration to run instead of throwing an exception
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    fun saveCreateSurveyRes(survey: CreateSurveyRes) {
        AppPreference.instance.lastTimeDataSaved = System.currentTimeMillis()
        val body = CreateSurveyBody(
            appConfig = Gson().toJson(survey.appConfig),
            appForms = Gson().toJson(survey.appForms)
        )
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(body)
        }
    }


    fun saveCreateSurveyRes(body: CreateSurveyBody) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(body)
        }
    }


    fun updateSurveyRes(surveyFile: SurveySingleResData) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm.copyToRealmOrUpdate(surveyFile) }
    }

    fun updateSurveyFileRes(surveyFile: SurveyFileRealm) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm.copyToRealmOrUpdate(surveyFile) }
    }

    fun removeSurveyFileRes(filePath: String?) {
        filePath?.let {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val result =
                    realm.where(SurveyFileRealm::class.java).equalTo("filePath", filePath).findAll()
                File(filePath).delete()
                result?.deleteAllFromRealm()
            }
        }
    }

    fun getCreateSurveyBody(): CreateSurveyBody? {
        val realm = Realm.getDefaultInstance()
        return realm.where(CreateSurveyBody::class.java).findFirst()
    }

    fun updateAppConfigData(appConfig : SurveyAppConfigData) {
        val realm = Realm.getDefaultInstance()
        val body = getCreateSurveyBody()
        realm.executeTransaction {
            body?.setAppConfigData(appConfig)
        }

        if (body != null)
            saveCreateSurveyRes(body)

    }

    fun updateAppFormData(formList: List<SurveyAppFormData>) {
        val realm = Realm.getDefaultInstance()
        val body = getCreateSurveyBody()
        realm.executeTransaction {
            body?.setFormDataList(formList)
        }

        if (body != null)
            saveCreateSurveyRes(body)

    }

    fun getSurveyResList(): List<SurveySingleResData> {
        val realm = Realm.getDefaultInstance()
        return realm.where(SurveySingleResData::class.java).findAll()
    }

    fun removePostSurveyList(list: List<SurveyPostReqBody>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            for (item in list) {
                val deleteItem = realm.where(SurveySingleResData::class.java)
                    .equalTo("uniqueId", item.uniqueId).findAll()
                deleteItem.deleteAllFromRealm()
            }
        }
    }

    fun getSurveyFileList(): List<SurveyFileRealm> {
        val realm = Realm.getDefaultInstance()
        return realm.where(SurveyFileRealm::class.java).findAll()
    }

    fun removeSurveyFile(uniqId: String?) {
        if (uniqId == null)
            return
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val deleteItem =
                realm.where(SurveyFileRealm::class.java).equalTo("uniqId", uniqId).findAll()
            val pathList = mutableListOf<String>()
            deleteItem?.forEach {
                val path = it.filePath
                if (!path.isNullOrEmpty())
                    pathList.add(path)
            }
            deleteItem.deleteAllFromRealm()
            pathList.forEach {
                AppUtils.removeImagePath(it)
            }
        }
    }

    fun getSurveyResCount(): Long {
        val realm = Realm.getDefaultInstance()
        return realm.where(SurveySingleResData::class.java).count()
    }

    fun getSurveyFileCount(): Long {
        val realm = Realm.getDefaultInstance()
        return realm.where(SurveyFileRealm::class.java).count()
    }


    fun clearRealmData() {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction {
                realm.deleteAll()
            }
        } catch (e: Exception) {
        }
    }

}
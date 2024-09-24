package dev.lucas.poweredFarm.database.models

interface IModel<T> {
    fun save()
    fun delete(): Boolean
}
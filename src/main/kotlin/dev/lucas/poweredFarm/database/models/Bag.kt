package dev.lucas.poweredFarm.database.models

data class Bag(val user: User, val crop: Crop, val amount: Int, val limit: Int)

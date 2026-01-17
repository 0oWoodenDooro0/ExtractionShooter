package com.gmail.vincent031525.extractionshooter.health

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Pose
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object LimbAABBHelper {

    fun getTargetPart(entity: LivingEntity, hitVec: Vec3?): BodyPart? {
        val box = entity.boundingBox
        if (hitVec == null) return BodyPart.BODY

        return when (entity.pose) {
            Pose.STANDING -> getStandPart(entity, box, hitVec)
            Pose.CROUCHING -> getCrouchPart(entity, box, hitVec)
            Pose.SWIMMING -> getPronePart(entity, hitVec)
            else -> null
        }
    }

    private fun getStandPart(entity: LivingEntity, box: AABB, hitVec: Vec3): BodyPart {
        val height = entity.bbHeight
        val relativeY = hitVec.y - box.minY
        val pct = relativeY / height

        return when {
            pct > 0.775 -> BodyPart.HEAD
            pct > 0.387 -> BodyPart.BODY
            else -> BodyPart.LEGS
        }
    }

    private fun getCrouchPart(entity: LivingEntity, box: AABB, hitVec: Vec3): BodyPart {
        val height = entity.bbHeight
        val relativeY = hitVec.y - box.minY
        val pct = relativeY / height

        return when {
            pct > 0.667 -> BodyPart.HEAD
            pct > 0.333 -> BodyPart.BODY
            else -> BodyPart.LEGS
        }
    }

    private fun getPronePart(entity: LivingEntity, hitVec: Vec3): BodyPart {
        val lookVec = Vec3.directionFromRotation(0f, entity.yRot).normalize()
        val relVec = hitVec.subtract(entity.position())
        val dist = relVec.dot(lookVec)

        return when {
            dist > 1.4 -> BodyPart.HEAD
            dist > 0.5 -> BodyPart.BODY
            else -> BodyPart.LEGS
        }
    }
}

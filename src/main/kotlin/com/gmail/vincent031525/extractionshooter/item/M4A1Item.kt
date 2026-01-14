package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.client.renderer.M4A1Renderer
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.client.GeoRenderProvider
import software.bernie.geckolib.animatable.manager.AnimatableManager
import software.bernie.geckolib.animation.AnimationController
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.animation.`object`.PlayState
import software.bernie.geckolib.animation.state.AnimationTest
import software.bernie.geckolib.renderer.GeoItemRenderer
import java.util.function.Consumer


class M4A1Item(properties: Properties) : GunItem(properties) {

    override val gunStats = GunStats(
        damage = 5.0f,
        rps = 10,
        range = 100.0,
        verticalRecoil = 1.2f,
        horizontalRecoil = 0.5f
    )

    override fun createGeoRenderer(consumer: Consumer<GeoRenderProvider>) {
        consumer.accept(object : GeoRenderProvider {
            private var renderer: M4A1Renderer? = null

            override fun getGeoItemRenderer(): GeoItemRenderer<*> {
                return renderer ?: M4A1Renderer()
            }
        })
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        val controller =
            AnimationController("shoot_controller", 0) { state: AnimationTest<GeoAnimatable> -> PlayState.STOP }

        controller.animationSpeed = gunStats.animationSpeed
        controller.triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))

        controllers.add(controller)
    }
}
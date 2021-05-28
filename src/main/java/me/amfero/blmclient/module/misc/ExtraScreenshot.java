package me.amfero.blmclient.module.misc;

import java.awt.image.BufferedImage;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.util.imgurutil.ImgurUploader;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExtraScreenshot extends Module {
	
	public ExtraScreenshot()
	{
		super("ExtraScreenshot", "", Category.MISC);
	}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onScreenshot(ScreenshotEvent event){
    	ImgurUploader imgurUploader = new ImgurUploader();
    	BufferedImage screenshot = event.getImage();
    	imgurUploader.uploadImage(screenshot);
    }

}
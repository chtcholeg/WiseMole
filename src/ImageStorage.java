/*
 * Copyright (C) 2022 The Java Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * The {ImageStorage} is a storage of images that are used in the application
 *
 * @author olegshchepilov
 *
 */

public class ImageStorage {

	static public Image getImage(String resourceId) {
		Image result = getInstance().images.get(resourceId);
		if (result != null) {
			return result;
		}
		result = getInstance().loadImage(resourceId);
		getInstance().images.put(resourceId, result);
		return result;
	}
	
	private Image loadImage(String resourceId) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream(resourceId);
			return ImageIO.read(input);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static ImageStorage getInstance() {
		if (instance == null) {
			instance = new ImageStorage();
		}
		return instance;
	}
	
	private Map<String, Image> images = new HashMap<String, Image>();
	static ImageStorage instance = null;
}

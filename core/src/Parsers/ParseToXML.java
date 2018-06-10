package Parsers;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Graphics.ObjectManager;
import Graphics.Object;


public class ParseToXML {

	
	public ParseToXML()
	{
		
	}
	
	public String Parse(String filename)
	{
		
		if(ObjectManager.objects.size() == 0) return "Failed [No elements]";
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Level");
			doc.appendChild(rootElement);

			for(int i = 0; i < ObjectManager.objects.size(); i++)
			{
				Object object = ObjectManager.objects.get(i);
				
			// Object elements
			Element Object = doc.createElement("Object");
			rootElement.appendChild(Object);

			// Position elements
			Element Position = doc.createElement("Position");
			
			// X pos
			Element pos_X = doc.createElement("x");
			pos_X.appendChild(doc.createTextNode(Float.toString(object.getPosition().x)));
			Position.appendChild(pos_X);
			
			//Y pos
			Element pos_Y = doc.createElement("y");
			pos_Y.appendChild(doc.createTextNode(Float.toString(object.getPosition().y)));
			Position.appendChild(pos_Y);
			
			Object.appendChild(Position);
			
			// Texture elements
			Element texture = doc.createElement("Texture");
			texture.appendChild(doc.createTextNode(object.getFileName()));
			Object.appendChild(texture);


			// Color elements
			Element Color = doc.createElement("Color");
			
			Element red = doc.createElement("Red");
			red.appendChild(doc.createTextNode(Float.toString(object.getColor().r)));
			Color.appendChild(red);
			
			Element green = doc.createElement("Green");
			green.appendChild(doc.createTextNode(Float.toString(object.getColor().g)));
			Color.appendChild(green);
			
			Element blue = doc.createElement("Blue");
			blue.appendChild(doc.createTextNode(Float.toString(object.getColor().b)));
			Color.appendChild(blue);
			
			Element alpha = doc.createElement("Alpha");
			alpha.appendChild(doc.createTextNode(Float.toString(object.getColor().a)));
			Color.appendChild(alpha);
			
			
			Object.appendChild(Color);

			
			
			// Collision setting elements
			Element collisionSetting = doc.createElement("CollisionSetting");
			collisionSetting.appendChild(doc.createTextNode(object.getOnCollision()));
			Object.appendChild(collisionSetting);

		
			// Size elements
			Element size = doc.createElement("Size");
			size.appendChild(doc.createTextNode(Float.toString(object.getSize())));
			Object.appendChild(size);

			// Rotation elements
			Element rotation = doc.createElement("Rotation");
			rotation.appendChild(doc.createTextNode(Float.toString(object.getRotation())));
			Object.appendChild(rotation);
			
			
			// Entity type elements
			Element type = doc.createElement("Type");
			type.appendChild(doc.createTextNode(object.getType().toString()));
			Object.appendChild(type);
			
			// TextureID ( Used by level editor only ) elements
			Element textureTableID = doc.createElement("TextureTableID");
			textureTableID.appendChild(doc.createTextNode(Integer.toString(object.getTextureIDFromTextureTable())));
			Object.appendChild(textureTableID);
			
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("../core/assets/Levels/" + filename + ".xml"));

			transformer.transform(source, result);

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return "Failed [configException]";
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
			return "Failed [transformException]";
		  }
		return "Saved successfully";
		}

	
	}
	


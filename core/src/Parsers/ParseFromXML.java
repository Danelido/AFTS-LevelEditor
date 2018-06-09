package Parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import Graphics.Object;
import Graphics.ObjectManager;
import Properties.EntityType;

public class ParseFromXML {

	private ArrayList<Object> tempObjectList = new ArrayList<Object>();
	
	public ParseFromXML()
	{
		
	}
	
	public String Parse(String filename)
	{
		//Get Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
		//Build Document
		Document document = builder.parse(new File("../core/assets/Levels/"+ filename + ".xml"));
		 
		//Normalize the XML Structure; It's just too important !!
		document.getDocumentElement().normalize();
		 
		//Here comes the root node
		Element root = document.getDocumentElement();
		System.out.println(root.getNodeName());
		 
		//Get all employees
		NodeList nList = document.getElementsByTagName("Object");
		System.out.println("============================");
		
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
		 Node node = nList.item(temp);
		 System.out.println("");    //Just a separator
		 if (node.getNodeType() == Node.ELEMENT_NODE)
		 {
		    //Print each object detail
		    Element eElement = (Element) node;
		    
		    Vector2 position = new Vector2(
		    		Float.parseFloat(eElement.getElementsByTagName("x").item(0).getTextContent()),
		    		Float.parseFloat(eElement.getElementsByTagName("y").item(0).getTextContent()));
	 
		    String textureFileName = eElement.getElementsByTagName("Texture").item(0).getTextContent();
		    Texture texture = new Texture(Gdx.files.internal("Textures/" + textureFileName));
		 
		    Color color = new Color();
		    color.r = Float.parseFloat(eElement.getElementsByTagName("Red").item(0).getTextContent());
		    color.g = Float.parseFloat(eElement.getElementsByTagName("Green").item(0).getTextContent());
		    color.b = Float.parseFloat(eElement.getElementsByTagName("Blue").item(0).getTextContent());
		    color.a = Float.parseFloat(eElement.getElementsByTagName("Alpha").item(0).getTextContent());
		
		    String collisionSetting = eElement.getElementsByTagName("CollisionSetting").item(0).getTextContent();
		    
		    float size = Float.parseFloat(eElement.getElementsByTagName("Size").item(0).getTextContent());
		    float rotation = Float.parseFloat(eElement.getElementsByTagName("Rotation").item(0).getTextContent());
		    int tableTextureID = Integer.parseInt(eElement.getElementsByTagName("TextureTableID").item(0).getTextContent());
		    
		    Object object = new Object(texture,textureFileName , EntityType.BLOCK);
		    object.setPosition(position);
		    object.setSize(size);
		    object.setColor(color);
		    object.setOnCollision(collisionSetting);
		    object.setRotation(rotation);
		    object.setTextureIDFromTextureTable(tableTextureID);
		    this.tempObjectList.add(object);
		    
		 }
		}
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed [configException]";
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed [SAXException]";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed [No such file]";
		}
		 
		ObjectManager.objects.clear();
		
		for(int i = 0; i < this.tempObjectList.size(); i++)
		{
			ObjectManager.objects.add(new Object(this.tempObjectList.get(i)));
		}
		this.tempObjectList.clear();
		return "Successfully loaded";
	}
	
}

package Parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import Properties.EntityObject;
import Properties.EntityType;

public class ReadEntityObjectsFromFile {
	
	private final String FILENAME = "EntityObjects.txt";
	private ArrayList<EntityObject> objects = new ArrayList<EntityObject>();
	
	private BufferedReader buffer;
	
	public ReadEntityObjectsFromFile()
	{	
		try {
			this.buffer = new BufferedReader(new FileReader("../core/assets/Config/" +FILENAME));
			
			String line = buffer.readLine();
			
			while( line != null)
			{
				String textureAttrib = this.processLine(line, buffer);
				line = buffer.readLine();
				
				String customNameAttrib = this.processLine(line, buffer);
				line = buffer.readLine();
				
				//String typeAttrib = this.processLine(line, buffer);
				//line = buffer.readLine();
				
				this.objects.add(new EntityObject(
						this.getTexture(textureAttrib),
						textureAttrib,
						customNameAttrib,
						EntityType.BLOCK
						));
				
			}
			
			buffer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Manually add spawnpoint and finishpoint because no need to have them in file
		this.objects.add(new EntityObject(
				new Texture(Gdx.files.internal("Textures/player_start.png")),
				"Textures/player_start.png",
				"Player spawn",
				EntityType.SPAWNPOINT
				));
		
		this.objects.add(new EntityObject(
				new Texture(Gdx.files.internal("Textures/player_finish.png")),
				"Textures/player_finish.png",
				"Player finish",
				EntityType.FINISHPOINT
				));
	}
	
	
	private String processLine(String line, BufferedReader buffer) throws IOException
	{
		// TextureName
		String[] chunks = line.split(":");
		while(chunks.length < 2) {
			// Get rid of the empty spaces
			line = buffer.readLine();
			chunks = line.split(":");	
		}
		
		String newLine = chunks[1];
		
		// get rid of the first space if the is one
		if(newLine.charAt(0) == ' ')
		{
			newLine.substring(1);
		}
		
		String processedLine = newLine.trim();

		return processedLine;
		
	}
	
	private Texture getTexture(String name)
	{
		return new Texture(Gdx.files.internal("Textures/"+name));
	}
	
	private EntityType getEntityType(String name)
	{
		if(name.equalsIgnoreCase(EntityType.BLOCK.toString()))
		{
			return EntityType.BLOCK;
		}
		
		if(name.equalsIgnoreCase(EntityType.SPAWNPOINT.toString()))
		{
			return EntityType.SPAWNPOINT;
		}
		
		if(name.equalsIgnoreCase(EntityType.FINISHPOINT.toString()))
		{
			return EntityType.FINISHPOINT;
		}
		
		return EntityType.__error__;
	}
	
	public ArrayList<EntityObject> getEntityObjectList()
	{
		return this.objects;
	}
	
}

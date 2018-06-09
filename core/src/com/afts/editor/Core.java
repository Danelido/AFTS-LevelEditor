package com.afts.editor;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

import EditorPanel.EditPanel;
import Graphics.GraphicState;

public class Core extends ApplicationAdapter {
	
	public final static float WINDOW_WIDTH = 1280.f;
	public final static float WINDOW_HEIGHT = 720.f;
	
	private EditPanel editpanel;
	private GraphicState graphicState;
	private InputProcessor p1, p2;
	private InputMultiplexer mulitplexer;
	@Override
	public void create () {
		this.editpanel = new EditPanel();
		this.graphicState = new GraphicState(this.editpanel);
		
		this.p1 = this.editpanel.getStage();
		this.p2 = (InputProcessor) this.graphicState;
		
		this.mulitplexer = new InputMultiplexer();
		this.mulitplexer.addProcessor(p1);
		this.mulitplexer.addProcessor(p2);
		
		Gdx.input.setInputProcessor(this.mulitplexer);
	}

	@Override
	public void render () {
		
		Gdx.gl.glClearColor(100.f/255.f, 100.f/255.f, 100.f/ 255.f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.graphicState.update();
		this.graphicState.render();
		this.editpanel.draw();
		
	}
	
	@Override
	public void dispose () {
		this.editpanel.dispose();
		this.graphicState.dispose();
	}
}

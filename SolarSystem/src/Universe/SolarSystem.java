package Universe;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureData.Flusher;
import com.jogamp.opengl.util.texture.TextureIO;

public class SolarSystem implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private float obsX, obsY, obsZ, upX, upY, rotX, rotY;
	private GL2 gl;
	private GLU glu;
	private GLUT glut;
	private GLAutoDrawable glDrawable;
	private Texture tex, texSun, textMerc, texVen, texEarth, texMars, texJup, texSat, texUran, texNep;
	private BufferedImage imagem;
	private int width, height;
	private TextureData td;
	private ByteBuffer buffer;
	private int idTexture[];
	boolean b = true;
	float orbiting_speed[] = { 4f, 2f, 1.4f, 1.1f, 0.9f, 0.75f, 0.63f, 0.54f };
	float angles[] = new float[9];
	float[] ambient = { 0.0f, 0.0f, 0.0f, 1.0f };
	float[] diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] specular = { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] position = { -1f, 1.0f, 0f, 0.0f };
	List<Float> list = new ArrayList<Float>();

	public static void main(String[] args) {
		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities cap = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(cap);

		JFrame frame = new JFrame("Sistema Solar");
		frame.setSize(1280, 720);
		frame.add(canvas);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		SolarSystem objSS = new SolarSystem();
		canvas.addGLEventListener(objSS);
		canvas.addKeyListener(objSS);
		canvas.setVisible(true);

		final FPSAnimator animator = new FPSAnimator(canvas, 90);
		animator.start();

	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			upY += 0.01f;
			break;
		case KeyEvent.VK_S:
			upY -= 0.01f;
			break;
		case KeyEvent.VK_A:
			upX -= 0.01f;
			break;
		case KeyEvent.VK_D:
			upX += 0.01f;
			break;
		case KeyEvent.VK_DOWN:
			obsZ += 5f;
			break;
		case KeyEvent.VK_UP:
			obsZ -= 5f;
			break;
		case KeyEvent.VK_Q:
			rotY -= 1f;
			break;
		case KeyEvent.VK_E:
			rotY += 1f;
			break;
		case KeyEvent.VK_P:
			if (b == true) {
				b = false;
			} else {
				b = true;
			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable arg0) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		setCamera();
		lightning();
		/*sun();
		angle_update();
		mercury();
		venus();
		earth();
		mars();
		jupyter();
		saturn();
		uranus();
		neptune();*/
		stars(100);

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		glDrawable = drawable;
		gl = drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		// Habilita o modelo de colorização de Gouraud
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_AUTO_NORMAL);
		gl.glEnable(GL2.GL_NORMALIZE);

		gl.glEnable(GL2.GL_BLEND); // Ativar mistura.
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		obsX = 0;
		obsY = 10;
		rotX = 0;
		rotY = 0;
		obsZ = 300;

		// Habilita o depth-buffering
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LESS);

		texSun = carregaImagem("sun.jpg");
		textMerc = carregaImagem("mercury.jpg");
		texVen = carregaImagem("venus.jpg");
		texEarth = carregaImagem("earth.jpg");
		texMars = carregaImagem("mars.jpg");
		texJup = carregaImagem("jupyter.jpg");
		texSat = carregaImagem("saturn.jpg");
		texUran = carregaImagem("uranus.jpg");
		texNep = carregaImagem("neptune.jpg");
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void setCamera() {
		// Especifica sistema de coordenadas de projeção
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// Inicializa sistema de coordenadas de projeção
		gl.glLoadIdentity();

		// Especifica a projeção perspectiva(angulo,aspecto,zMin,zMax)
		// aspecto = width/hight
		upY = -0.15f;
		glu.gluPerspective(9, 1.7, 1, 9000);
		glu.gluLookAt(0, 0, 3, upX, upY, 0, 0, 1, 0);
		Observador();

	}

	public void Observador() {

		gl.glTranslatef(-obsX, -obsY, -obsZ);
		gl.glRotatef(rotY, 0, 1, 0);
		gl.glRotatef(45, 1, 0, 0);
	}

	public Texture carregaImagem(String fileName) {

		imagem = null;
		try {
			imagem = ImageIO.read(new File("src/Universe//" + fileName));

			width = imagem.getWidth();
			height = imagem.getHeight();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro na leitura do arquivo " + fileName);
			System.out.println(e.getStackTrace());
		}

		try {
			gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
			InputStream stream = getClass().getResourceAsStream(fileName);
			td = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
			tex = TextureIO.newTexture(td);

		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		// ...e obtém um ByteBuffer a partir dela

		buffer = (ByteBuffer) td.getBuffer();

		idTexture = new int[10];
		gl.glGenTextures(1, idTexture, 1);

		// Especifica qual é a textura corrente pelo identificador
		gl.glBindTexture(GL.GL_TEXTURE_2D, idTexture[0]);

		// Envio da textura para OpenGL
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, width, height, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);

		// Define os filtros

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		return tex;

	}

	public void lightning() {
		// parâmetros da luz

		// luz1
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

		float[] model_ambient = { 0.4f, 0.4f, 0.4f, 1.0f };
		int model_two_side = 1;

		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, model_ambient, 0);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		// Ativa o uso da luz ambiente
		// gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, model_ambient, 0);
		// Habilita o uso de iluminação
		gl.glEnable(GL2.GL_LIGHTING);
		// Habilita a luz de número 0
		gl.glEnable(GL2.GL_LIGHT0);

		// reflexão da luz nos planetas

		float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] mat_ambient = { 0.7f, 0.7f, 0.7f, 1.0f };
		float[] mat_ambient_color = { 0.8f, 0.8f, 0.2f, 1.0f };
		float[] mat_diffuse = { 0.1f, 0.5f, 0.8f, 1.0f };
		float[] mat_specular = { 1.0f, 1.0f, 1.0f, 1.0f };
		float no_shininess = 0.0f;
		float low_shininess = 5.0f;
		float high_shininess = 100.0f;
		float[] mat_emission = { 0.3f, 0.2f, 0.2f, 0.0f };

		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
		gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);

	}

	public void sun() {

		float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] mat_ambient_color = { 0.8f, 0.8f, 0.2f, 1.0f };
		float[] mat_diffuse = { 0.1f, 0.5f, 0.8f, 1.0f };
		float[] mat_specular = { 1.0f, 1.0f, 1.0f, 1.0f };
		float low_shininess = 5.0f;

		texSun.enable(gl);
		texSun.bind(gl);

		gl.glPushMatrix();

		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient_color, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
		gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, low_shininess);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);

		GLUquadric sun = glu.gluNewQuadric();
		glu.gluQuadricTexture(sun, true);
		glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
		glu.gluQuadricNormals(sun, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sun, GLU.GLU_OUTSIDE);
		glu.gluSphere(sun, 3f, 100, 100);
		glu.gluDeleteQuadric(sun);
		gl.glPopMatrix();

		texSun.disable(gl);

	}

	public void mercury() {

		// orbita
		gl.glPushMatrix();
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 361; i++) {
			gl.glVertex3f(5f * (float) Math.sin(i * Math.PI / 180), 0, 5f * (float) Math.cos(i * Math.PI / 180));
		}
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		textMerc.enable(gl);
		textMerc.bind(gl);

		float l_diffuse[] = { 0.7f, 0.7f, 0.7f, 1.0f };
		// create planet
		gl.glPushMatrix();
		gl.glRotatef(angles[0], 0, 1, 0);
		gl.glTranslatef(5f, 0.0f, 0.0f);
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		GLUquadric merc = glu.gluNewQuadric();
		glu.gluQuadricTexture(merc, true);
		glu.gluQuadricDrawStyle(merc, GLU.GLU_FILL);
		glu.gluQuadricNormals(merc, GLU.GLU_SMOOTH);
		glu.gluQuadricOrientation(merc, GLU.GLU_OUTSIDE);
		glu.gluSphere(merc, 0.8f, 100, 100);
		//glu.gluDeleteQuadric(merc);

		gl.glPopMatrix();

		textMerc.disable(gl);

	}

	public void angle_update() {
		for (int ii = 0; ii < 8; ii++) {
			angles[ii] = angles[ii] + orbiting_speed[ii];
			// moon_angle[ii]= moon_angle[ii] + moon_speed[ii];
		}

	}

	public void venus() {

		// orbita
		gl.glPushMatrix();
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 361; i++) {
			gl.glVertex3f(8f * (float) Math.sin(i * Math.PI / 180), 0, 8f * (float) Math.cos(i * Math.PI / 180));
		}
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		texVen.enable(gl);
		texVen.bind(gl);

		// create planet
		gl.glPushMatrix();
		gl.glRotatef(angles[1], 0, 1, 0);
		gl.glTranslatef(8.0f, 0.0f, 0.0f);
		gl.glColor3f(0.8f, 0.8f, 0.8f);

		GLUquadric ven = glu.gluNewQuadric();
		glu.gluQuadricTexture(ven, true);
		glu.gluQuadricDrawStyle(ven, GLU.GLU_FILL);
		glu.gluQuadricNormals(ven, GLU.GLU_SMOOTH);
		glu.gluQuadricOrientation(ven, GLU.GLU_OUTSIDE);
		glu.gluSphere(ven, 1.4f, 100, 100);
		glu.gluDeleteQuadric(ven);
		gl.glPopMatrix();

		texVen.disable(gl);

	}

	public void earth() {
		// orbita
				gl.glPushMatrix();
				gl.glColor3f(0.25f, 0.25f, 0.25f);
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glBegin(GL.GL_LINE_STRIP);

				for (int i = 0; i < 361; i++) {
					gl.glVertex3f(12f * (float) Math.sin(i * Math.PI / 180), 0, 12f * (float) Math.cos(i * Math.PI / 180));
				}
				gl.glEnd();
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glPopMatrix();

				texEarth.enable(gl);
				texEarth.bind(gl);

				// create planet
				gl.glPushMatrix();
				gl.glRotatef(angles[2], 0, 1, 0);
				gl.glTranslatef(12.0f, 0.0f, 0.0f);
				gl.glColor3f(0.8f, 0.8f, 0.8f);

				GLUquadric earth = glu.gluNewQuadric();
				glu.gluQuadricTexture(earth, true);
				glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
				glu.gluQuadricNormals(earth, GLU.GLU_SMOOTH);
				glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
				glu.gluSphere(earth, 1.6f, 100, 100);
				//glu.gluDeleteQuadric(earth);
				gl.glPopMatrix();

				texEarth.disable(gl);
	}
	
	public void mars() {
		// orbita
				gl.glPushMatrix();
				gl.glColor3f(0.25f, 0.25f, 0.25f);
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glBegin(GL.GL_LINE_STRIP);

				for (int i = 0; i < 361; i++) {
					gl.glVertex3f(16f * (float) Math.sin(i * Math.PI / 180), 0, 16 * (float) Math.cos(i * Math.PI / 180));
				}
				gl.glEnd();
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glPopMatrix();

				texMars.enable(gl);
				texMars.bind(gl);

				// create planet
				gl.glPushMatrix();
				gl.glRotatef(angles[3], 0, 1, 0);
				gl.glTranslatef(16.0f, 0.0f, 0.0f);
				gl.glColor3f(0.8f, 0.8f, 0.8f);

				GLUquadric mars = glu.gluNewQuadric();
				glu.gluQuadricTexture(mars, true);
				glu.gluQuadricDrawStyle(mars, GLU.GLU_FILL);
				glu.gluQuadricNormals(mars, GLU.GLU_SMOOTH);
				glu.gluQuadricOrientation(mars, GLU.GLU_OUTSIDE);
				glu.gluSphere(mars, 1.1f, 100, 100);
				//glu.gluDeleteQuadric(earth);
				gl.glPopMatrix();

				texMars.disable(gl);
	}
	
	public void jupyter() {
		
		// orbita
		gl.glPushMatrix();
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 361; i++) {
			gl.glVertex3f(20f * (float) Math.sin(i * Math.PI / 180), 0, 20f * (float) Math.cos(i * Math.PI / 180));
		}
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		texJup.enable(gl);
		texJup.bind(gl);

		// create planet
		gl.glPushMatrix();
		gl.glRotatef(angles[4], 0, 1, 0);
		gl.glTranslatef(20.0f, 0.0f, 0.0f);
		gl.glColor3f(0.8f, 0.8f, 0.8f);

		GLUquadric jupyter = glu.gluNewQuadric();
		glu.gluQuadricTexture(jupyter, true);
		glu.gluQuadricDrawStyle(jupyter, GLU.GLU_FILL);
		glu.gluQuadricNormals(jupyter, GLU.GLU_SMOOTH);
		glu.gluQuadricOrientation(jupyter, GLU.GLU_OUTSIDE);
		glu.gluSphere(jupyter, 2f, 100, 100);
		//glu.gluDeleteQuadric(earth);
		gl.glPopMatrix();

		texJup.disable(gl);
	}
	
	public void saturn() {
		
		// orbita
				gl.glPushMatrix();
				gl.glColor3f(0.25f, 0.25f, 0.25f);
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glBegin(GL.GL_LINE_STRIP);

				for (int i = 0; i < 361; i++) {
					gl.glVertex3f(26f * (float) Math.sin(i * Math.PI / 180), 0, 26f * (float) Math.cos(i * Math.PI / 180));
				}
				gl.glEnd();
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glPopMatrix();

				texSat.enable(gl);
				texSat.bind(gl);

				// create planet
				gl.glPushMatrix();
				gl.glRotatef(angles[5], 0, 1, 0);
				gl.glTranslatef(26.0f, 0.0f, 0.0f);
				gl.glColor3f(0.8f, 0.8f, 0.8f);

				GLUquadric sat = glu.gluNewQuadric();
				glu.gluQuadricTexture(sat, true);
				glu.gluQuadricDrawStyle(sat, GLU.GLU_FILL);
				glu.gluQuadricNormals(sat, GLU.GLU_SMOOTH);
				glu.gluQuadricOrientation(sat, GLU.GLU_OUTSIDE);
				glu.gluSphere(sat, 1.9f, 100, 100);
				
				gl.glPushMatrix();
				gl.glColor3f(1f, 0.76f, 0.3f);
				gl.glRotatef(50, 1, 0, 0);
				glut.glutSolidTorus(0.3f,3f, 100, 100);
				
				gl.glPopMatrix();
				
				gl.glPopMatrix();

				texSat.disable(gl);
	}
	
	public void uranus() {
		
		// orbita
		gl.glPushMatrix();
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 361; i++) {
			gl.glVertex3f(32f * (float) Math.sin(i * Math.PI / 180), 0, 32f * (float) Math.cos(i * Math.PI / 180));
		}
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		texUran.enable(gl);
		texUran.bind(gl);

		// create planet
		gl.glPushMatrix();
		gl.glRotatef(angles[6], 0, 1, 0);
		gl.glTranslatef(32.0f, 0.0f, 0.0f);
		gl.glColor3f(0.8f, 0.8f, 0.8f);

		GLUquadric uran = glu.gluNewQuadric();
		glu.gluQuadricTexture(uran, true);
		glu.gluQuadricDrawStyle(uran, GLU.GLU_FILL);
		glu.gluQuadricNormals(uran, GLU.GLU_SMOOTH);
		glu.gluQuadricOrientation(uran, GLU.GLU_OUTSIDE);
		glu.gluSphere(uran, 1.8f, 100, 100);
			
		gl.glPopMatrix();

		texUran.disable(gl);
	}
	
	public void neptune () {
		
		// orbita
				gl.glPushMatrix();
				gl.glColor3f(0.25f, 0.25f, 0.25f);
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glBegin(GL.GL_LINE_STRIP);

				for (int i = 0; i < 361; i++) {
					gl.glVertex3f(36f * (float) Math.sin(i * Math.PI / 180), 0, 36f * (float) Math.cos(i * Math.PI / 180));
				}
				gl.glEnd();
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glPopMatrix();

				texNep.enable(gl);
				texNep.bind(gl);

				// create planet
				gl.glPushMatrix();
				gl.glRotatef(angles[7], 0, 1, 0);
				gl.glTranslatef(36.0f, 0.0f, 0.0f);
				gl.glColor3f(0.8f, 0.8f, 0.8f);

				GLUquadric nep = glu.gluNewQuadric();
				glu.gluQuadricTexture(nep, true);
				glu.gluQuadricDrawStyle(nep, GLU.GLU_FILL);
				glu.gluQuadricNormals(nep, GLU.GLU_SMOOTH);
				glu.gluQuadricOrientation(nep, GLU.GLU_OUTSIDE);
				glu.gluSphere(nep, 1.7f, 100, 100);
					
				gl.glPopMatrix();

				texNep.disable(gl);

	}
	
	public void stars(int NumberOfPoints) {
		
		float x[] = new float [NumberOfPoints];
		float y[] = new float [NumberOfPoints];
		
		gl.glColor3f(1f, 1f, 1f);
		gl.glBegin( GL.GL_POINTS );

		   gl.glVertex2f( 1f,1f );

		
		gl.glEnd();
     }
	}



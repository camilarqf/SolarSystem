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
import com.jogamp.opengl.util.texture.TextureIO;

public class SolarSystem implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private float obsX, obsY, obsZ, upX, upY, rotX, rotY;
	private GL2 gl;
	private GLU glu;
	private GLUT glut;
	private GLAutoDrawable glDrawable;
	private Texture tex, texSun, textMerc;
	private BufferedImage imagem;
	private int width, height;
	private TextureData td;
	private ByteBuffer buffer;
	private int idTexture[];

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
		sun();
		mercury();

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
		obsZ = 4000;
		// habilita a reflexão no material
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_DIFFUSE);
		// Habilita o uso de iluminação
		gl.glEnable(GL2.GL_LIGHTING);
		// Habilita a luz de número 0
		gl.glEnable(GL2.GL_LIGHT0);
		// Habilita o depth-buffering
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LESS);

		texSun = carregaImagem("sun.jpg");
		textMerc = carregaImagem("mercury.jpg");
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
		glu.gluPerspective(9, 1.7, 10, 8000);
		glu.gluLookAt(0, 0, 3, upX, upY, 0, 0, 1, 0);
		Observador();

	}

	public void Observador() {

		gl.glTranslatef(-obsX, -obsY, -obsZ);
		gl.glRotatef(rotY, 0, 1, 0);
		gl.glRotatef(55, 1, 0, 0);
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

	public void sun() {

		// parâmetros da luz
		float l_Ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
		float l_diffuse[] = { 0.7f, 0.7f, 0.7f, 1.0f };
		float l_specular[] = { 1f, 1f, 1f, 1.0f };
		float positionLight[] = { -1f, 0f, 0f, 0f };

		// Ativa o uso da luz ambiente
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, l_Ambient, 0);

		// Capacidade de brilho do sol
		float specularity[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		int sunshine = 80;
		float sunEmission[] = { 1f, 1f, 0f, 1.0f };
		float dir[] = { -1, 0, 0 };
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, l_Ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, l_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specularity, 0);
		gl.glMateriali(GL.GL_FRONT, GL2.GL_SHININESS, sunshine);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, sunEmission, 0);

		// Define os parâmetros da luz do sol -------------------
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, l_Ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, l_diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, l_specular, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, positionLight, 0);
		//gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, 28f);
				

		// carrega textura
		try {
			InputStream stream = getClass().getResourceAsStream("sun.jpg");
			TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
			tex = TextureIO.newTexture(data);
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		tex.enable(gl);
		tex.bind(gl);

		gl.glPushMatrix();
		// gl.glColor3f(1f, 1f, 1f);

		GLUquadric sun = glu.gluNewQuadric();
		glu.gluQuadricTexture(sun, true);
		glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
		glu.gluQuadricNormals(sun, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sun, GLU.GLU_OUTSIDE);
		glu.gluSphere(sun, 70f, 100, 100);
		glu.gluDeleteQuadric(sun);
		gl.glPopMatrix();

		tex.disable(gl);
		tex.destroy(gl);

	}

	public void mercury() {

		// orbita
		gl.glPushMatrix();
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		//gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 361; i++) {
			gl.glVertex3f(130f * (float) Math.sin(i * Math.PI / 180), 0, 130f * (float) Math.cos(i * Math.PI / 180));
		}
		gl.glEnd();
		//gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		// carrega textura
		try {
			InputStream stream = getClass().getResourceAsStream("mercury.jpg");
			TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
			tex = TextureIO.newTexture(data);
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		tex.enable(gl);
		tex.bind(gl);
		
		float l_diffuse[] = { 0.7f, 0.7f, 0.7f, 1.0f };
		
		gl.glPushMatrix();
		
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, l_diffuse, 0);
		gl.glTranslatef(130.0f, 0.0f, 0.0f);
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		GLUquadric merc = glu.gluNewQuadric();
		glu.gluQuadricTexture(merc, true);
		glu.gluQuadricDrawStyle(merc, GLU.GLU_FILL);
		glu.gluQuadricNormals(merc, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(merc, GLU.GLU_OUTSIDE);
		glu.gluSphere(merc, 24.5f, 100, 100);
		glu.gluDeleteQuadric(merc);
		glut.glutSolidSphere(24.5f, 100, 100);
		
		gl.glPopMatrix();

		tex.disable(gl);
		tex.destroy(gl);
		
		int test = 1;
		gl.glPushMatrix();
		gl.glRotatef(test, 0, 1, 0);
		test +=1;
		gl.glPopMatrix();
		
		// Flush the pipeline, and swap the buffers
		
		

	}

}

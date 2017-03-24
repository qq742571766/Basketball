package wyf.jsl.lin;

import javax.microedition.khronos.opengles.GL10;
import static wyf.jsl.lin.Constant.*;

public class LogicalBall{
	float vx;
	float vy;
	float vz;
	float timeLive;
	float startX;
	float startY;
	float startZ;
	BallForDraw ball;	
	Floor shadow;
	int state;
	
	float currentX;
	float currentY;
	float currentZ;
	
	float previousX;
	float previousY;
	float previousZ;	
	
	float xAngle=0;
	BasketballActivity activity;
	
	public LogicalBall(BallForDraw ball,Floor shadow,float startX,BasketballActivity activity)
	{
		this.ball=ball;
		this.shadow=shadow;
		this.startX=startX;
		this.activity=activity;
		
		startY=0;
		startZ=BALL_NEAREST_Z;
		state=2;
		
		vx=0f;
		vy=0f;
		vz=0f;
		
		currentX=startX; 
		currentY=startY;
		currentZ=startZ;
	}
	
	public void drawSelf(GL10 gl)
	{
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPushMatrix();
		gl.glTranslatef(currentX, SHADOW_Y, currentZ);
		shadow.drawSelf(gl);
		gl.glPopMatrix();		
		gl.glDisable(GL10.GL_BLEND);
		
		gl.glPushMatrix();
		gl.glTranslatef(0, Constant.BALL_SCALE, 0);//����׵���������
		gl.glTranslatef(currentX, currentY, currentZ);
		gl.glRotatef(xAngle, 1, 0, 0);
		ball.drawSelf(gl);		
		gl.glPopMatrix();		
	}
	
	public void move()
	{		
		if(state==0)
		{
			currentX=startX;
			currentY=startY;
			currentZ=startZ;
		}
		else if(state==1)
		{
			if(currentZ<BALL_NEAREST_Z)
			{
				currentZ=currentZ+BALL_ROLL_SPEED;
				xAngle=xAngle+BALL_ROLL_ANGLE;
			}
			else
			{
				currentZ=BALL_NEAREST_Z;
				
				startX=currentX;
				startY=currentY;
				startZ=currentZ;
				
				state=0;
			}
		}
		else if(state==2)
		{
			timeLive=timeLive+BALL_FLY_TIME_SPAN;
			float tempCurrentX=startX+vx*timeLive;
			float tempCurrentY=startY+vy*timeLive-0.5f*G*timeLive*timeLive;
			float tempCurrentZ=startZ+vz*timeLive;
			
			boolean backFlag=false;
			float[] ballCenter={tempCurrentX,tempCurrentY,tempCurrentZ};
			float[] point=CollisionUtil.breakPoint
			(
					ballCenter,
					BALL_SCALE,
					ringCenter,
					ringR
					
			);
			
			if(point!=null)
			{
				if(SOUND_FLAG)
				{
					activity.playSound(1, 0);
				}
				
				float[] vBefore={vx,vy,vz};				
				float[] vAfter=CollisionUtil.ballBreak(vBefore, ballCenter, point);
				
				vx=vAfter[0]+(float)Math.random()*0.3f;
				vy=vAfter[1]+0.5f*((currentY>ringCenter[1])?1:-1);
				vz=vAfter[2];
				timeLive=0;
				startX=currentX;
				startY=currentY;
				startZ=currentZ;				
				return;
			}
			
			
			
			if(!backFlag&&tempCurrentY<0||tempCurrentY>HEIGHT-BALL_SCALE)
			{
				backFlag=true;
				vy=vy-G*timeLive;
				vy=-vy;
				timeLive=0;
				startX=currentX;
				startY=currentY;
				startZ=currentZ;
			}
			if(!backFlag&&tempCurrentZ<-0.5f*LENGTH+BALL_SCALE||tempCurrentZ>BALL_NEAREST_Z)
			{
				backFlag=true;
				vz=-vz;
				vy=vy-G*timeLive;
				timeLive=0;
				startX=currentX;
				startY=currentY;
				startZ=currentZ;
			}
			
			if(!backFlag&&tempCurrentX>0.5f*WIDTH-BALL_SCALE||tempCurrentX<-0.5f*WIDTH+BALL_SCALE)
			{
				backFlag=true;
				vx=-vx;
				vy=vy-G*timeLive;
				timeLive=0;
				startX=currentX;
				startY=currentY;
				startZ=currentZ;
			}
			
			
			if(!backFlag)
			{
				previousX=currentX;
				previousY=currentY;
				previousZ=currentZ;
				
				currentX=tempCurrentX;
				currentY=tempCurrentY;
				currentZ=tempCurrentZ;				
				
				if(previousY>ringCenter[1]&&currentY<ringCenter[1]&&
				   Math.sqrt
				   (
						   (previousX-ringCenter[0])*(previousX-ringCenter[0])+
						   (previousZ-ringCenter[2])*(previousZ-ringCenter[2])
				   )<ringR &&
				   Math.sqrt
				   (
						   (currentX-ringCenter[0])*(currentX-ringCenter[0])+
						   (currentZ-ringCenter[2])*(currentZ-ringCenter[2])
				   )<ringR 
				)
				{
					score++;
				}
					
			}
			else
			{
				if(SOUND_FLAG)
				{
					activity.playSound(1, 0);
				}
				
				vx=ENERGY_LOSS*vx;
				vy=ENERGY_LOSS*vy;
				vz=ENERGY_LOSS*vz;
				
				float vTotal=(float)Math.sqrt(vx*vx+vy*vy+vz*vz);
                if(vTotal<0.1f&&currentY<2*BALL_SCALE)
                {
                	vx=0;
                	vy=0;
                	vz=0;
                	state=1;
                }
			}
		}
	}
}












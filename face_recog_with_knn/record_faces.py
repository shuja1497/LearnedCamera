import numpy as np
import cv2
cam  = cv2.VideoCapture(0) # instantiated a camera object to capture images 

# create a harr cascade object for face recognotion... to extract features
face_cas = cv2.CascadeClassifier('./haarcascade_frontalface_default.xml')

data  = []
ix = 0 # current frame number

while True :
	ret , frame  = cam.read()# read frame from camera ..returns a boolean value ret>>whether cam is working fine or not  .. frame contains img as np matrix 
	#if cam is working fine we proceed to extract the face 
	if ret == True:
		print 'cam working fine \n'
		# convert current frame  into grayscale 
		gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

		# apply harrcascade to detect faces  in the current frame 
		faces  = face_cas.detectMultiScale(gray , 1.3 , 5 ) # takes the frame .. and returns an object on the basis of no of faces it receives
		
		# every object contains location of the imaage 
		#x,y contains corner co-ordinates of the image 
		# width and height of img in w , h 

		# for each face object we get
		for (x , y , w , h) in faces:
			face_component = frame[y:y+h , x:x+w , :]
			fc = cv2.resize(face_component, (50,50))
			# cpture image after every  10 frames 
			# capture total of 20 images 
			if ix%10 == 0 and  len(data) < 20 :
				data.append(fc)

			cv2.rectangle(frame , (x,y) , (x+w , y+h) , (0,255,0) , 2)
		ix += 1
		cv2.imshow('frame' , frame)
		print ix , 'frames done'

		if cv2.waitKey(1)  ==  27 or  len(data) >= 20 :
			break
	else :
		print 'error'


cv2.destroyAllWindows()

data = np.asarray(data)

print data.shape

np.save('face_01',data)

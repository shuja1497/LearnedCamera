import numpy as np 
import cv2

cam  = cv2.VideoCapture(0)

face_cas = cv2.CascadeClassifier('./haarcascade_frontalface_default.xml')

font  = cv2.FONT_HERSHEY_SIMPLEX

# convert matrix into linear vector 
# gives 20 linear vectors
f_01 = np.load('./face_01.npy').reshape(20,50*50*3)
f_02 = np.load('./face_02.npy').reshape(20,50*50*3)

print f_01.shape 

names = {
	0 : 'shuja',
	1 : 'shuja_2'
}

labels  = np.zeros((40,1))

labels[:20 , :] = 0.0
labels[20:40, : ] = 1.0

data  = np.concatenate([f_01, f_02])

print data.shape ,  labels.shape


def distance(x1, x2):
    return np.sqrt(((x1-x2)**2).sum())
                       
def knn(x , train , targets  , k = 5 ):  
    m = train.shape[0]
    dist = []
    for ix  in range(m):
        # storing dist from each point in dist list
        dist.append(distance(x ,train[ix]))
        pass
    
    dist = np.asarray(dist)
    indx  = np.argsort(dist)
    #print indx
    #print dist[indx] # distances in sorted order 
    #print labels[indx]
    #retriving top k values 
    sorted_labels  = labels[indx][:k ]
    #print sorted_labels
    #print np.unique(sorted_labels , return_counts=True)# returns tuple with two list  of labels and their count 
    counts  = np.unique(sorted_labels , return_counts=True)
    # first we need to get the index of max count using argmax 
    #then by using that index we will acess the frist list of labels and find label at that index
    #print counts[0][np.argmax(counts[1])]
    return counts[0][np.argmax(counts[1])]
        
while True:

	ret , frame = cam.read()

	if ret == True :
		print 'cam working fine\n'
		gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
		faces  = face_cas.detectMultiScale(gray , 1.3 , 5 )

		for (x , y , w , h) in faces:
			face_component = frame[y:y+h , x:x+w , :]
			fc = cv2.resize(face_component, (50,50))

			lab = knn(fc.flatten() , data , labels )

			text = names[int(lab)]

			cv2.putText(frame , text , (x,y) , font , 1,(255,255,0) , 2)

			cv2.rectangle(frame , (x,y) , (x+w , y+h) , (0,0,255), 2)

		cv2.imshow('face recognition' , frame)

		if cv2.waitKey(1)  ==  27:
			break  

	else :
		print 'error\n'


cv2.destroyAllWindows()
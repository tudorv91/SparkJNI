program_NAME 			:= vectorOps
program_C_SRCS			:= $(wildcard *.c)
program_CPP_SRCS		:= $(wildcard *.cpp)
program_C_OBJS 			:= ${program_C_SRCS:.c=.o}
JAVA_JDK				:= /usr/lib/jvm/java-1.7.0-openjdk-amd64
program_INCLUDE_DIRS 	:= $(JAVA_JDK)/include $(JAVA_JDK)/include/linux 
program_LIBRARY_DIRS 	:= 
program_LIBRARIES 		:=  
program_STATIC_LIBS		:= 
DEFINES 				:= 
DEFINES_LINE			:= $(addprefix -D, $(DEFINES))

CFLAGS 	+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) 
CFLAGS	+= -std=c11 -Wall -m64 -lrt -lpthread -fopenmp -fPIC
CPPFLAGS	+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) 
CPPFLAGS	+= -shared -fPIC -std=c++11 -O3 -m64 -lrt -lpthread -fopenmp
LDFLAGS 	+= $(foreach librarydir,$(program_LIBRARY_DIRS),-L$(librarydir))
LDFLAGS 	+= $(foreach library,$(program_LIBRARIES),-l$(library))

all: $(program_NAME)

debug: CFLAGS += -DDEBUG
debug: $(program_C_OBJS)
	gcc -o $(program_NAME) $(program_C_OBJS) $(program_STATIC_LIBS) $(LDFLAGS) $(CFLAGS)

$(program_NAME):$(program_C_OBJS)
	g++ $(program_CPP_SRCS) -o $(program_NAME).so $(DEFINES_LINE) $(program_STATIC_LIBS) $(CPPFLAGS) $(program_C_OBJS) $(LDFLAGS)
	
clean:
	@- $(RM) $(program_NAME)
	@- $(RM) $(program_C_OBJS)

distclean: clean
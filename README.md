![funny george image](https://github.com/lookout/ImperiusGeorge/raw/master/libs/george.jpg)
# ImperiusGeorge
###Remote-control/execute Java on Android phones via Ruby

1. Download and compile (`ant build`) the project. Push the .jar file to `/data/local/tmp/test.jar`
2. Run `adb forward tcp:1337 tcp:1337`
3. Run `adb shell uiautomator runtest test.jar -c imperiusgeorge.TestServer -e port 1337`

**Send post requests to http://localhost:1337**! Pull open a browser and try hitting *localhost:7120/execute?on=tests.LanguageAdapterShould$TestClass&method=callReturnString&args=[]*

Also try using [ImperiusGem](https://github.com/lookout/ImperiusGem) to drive your Android via Ruby

##Code examples!
Code examples over at the [ImperiusGem](https://github.com/lookout/ImperiusGem) github page!

##Blog post!
We're on the internets! Check out the [blog post](http://hackers.lookout.com/2013/09/imperius-george/) on lookout hackers blog.

##How it works
We needed a way to drive the Java runtime via text HTTP Get requests. Most of the work is done in [LanguageAdapter.java](https://github.com/lookout/ImperiusGeorge/blob/master/src/imperiusgeorge/backend/LanguageAdapter.java). Here's where we make a single method `run` introspectively run any available method on any class *or object* with any arguments. `run` takes three `String` parameters, 'on', 'method', and 'arguments'.

1. `on` first tries to find a class via the `findClass()` method (It'll work if it's a fully-qualified
 class name like `com.android.uiautomator.core.UiDevice`). If that succeeds it'll run The method statically.
    * If the method is 'new' it'll run the constructor it can find with the given arguments.
    * If it can't find the class it'll try looking up the instance (see step 3)
2. It then loops through each `method` available, finding ones with the same name and matching number of arguments.
    * adaptArgs() then tries to adapt, convert, or cast the provided arguments to the method's contract
    * If it throws an `IllegalArgumentException` it tries to match more methods (to support overloading).
3. Finally it calls the method and calls `adaptReturn()` on the result
    * If the returned value is a `java.lang.*` object it'll json-serialize it, returning it back to ruby.
    * A planned improvement is to also return serializable arrays and maps.
    * If the object is not directly returnable it stores it in a `HashMap<String,Object>` and returns a unique hash key. That's how the third option for the 'on' parameter works.


**A note about bundled code**: UI-Automator is actually an Android system tool, invoked via `adb shell`. It loads compiled jars' classes and introspectively runs the parameter passed class (it's junit extended). Because of this architecture you can't include separate libs (oh, we tried). We wanted to use gson but chose  [simple-json](https://code.google.com/p/json-simple/) for simplicity. We also used [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) for the web server functionality. These libraries simplified development a lot! We bundled the source to help simplify yours too, it'll compile and run as-is.

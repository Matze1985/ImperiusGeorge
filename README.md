![funny george image](https://github.com/lookout/ImperiusGeorge/raw/master/libs/george.jpg)
### ImperiusGeorge
#####Remote-control/execute Java on Android phones via Ruby

1. Download and compile (`ant build`) the project. Push the .jar file to `/data/local/tmp/test.jar`
2. Run `adb forward tcp:#1337 tcp:#1337`
3. Run `adb shell uiautomator runtest test.jar -c imperiusgeorge.TestServer`
4. Add [the ImperiusGem gem](https://github.com/lookout/ImperiusGem) to your project (add this to your Gemfile): `gem 'imperiusgem', :git => 'git@github.com:lookout/ImperiusGem.git', :branch => 'master'`
5. Use `require 'imperiusgem'` in your ruby code.
6. That's it! Try some sample code!

####Code examples!
Code examples over at the [ImperiusGem](https://github.com/lookout/ImperiusGem) github page!
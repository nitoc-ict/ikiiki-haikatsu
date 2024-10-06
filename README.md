# About this Project
 - このプロジェクトは、ikiiki-haikatsuの完成版（UnityをKotlinに組み込み済み）プロジェクトです

## 困ったときは
 - File -> Reload All from Discをしましょう
 - File -> invalidate Caches... をしましょう
   - `Clear file system cache and Local History`にチェックを付けて、Invalid and Restart をしてください

## Warning
1. Unityを組み込む際は、エキスプローラのフォルダから直接、unityLibraryフォルダをrootディレクトリに置いてください
 - Unityプロジェクトをビルドすると、unityLibraryというフォルダがビルドの直下に作成されます
 - rootディレクトリは、MyApplicationUI1(このREADMEと同じ階層)です
2. unityLibraryディレクトリ内のlibsにある、unity-classes.jarをapp/libsにコピーしてください
 - 同名のファイルがあった場合、上書きしてください
3. unity-classes.jarを右クリックし、`Add to library`をしてください
 - okをそのまま押して問題ありません
 - これが出ない場合は、invalid caches をした後に、ProjectStructureのunity-classes.jar を削除し、Add to Libraryをしてください
4. build(Module:unityLibrary)の buildToolsVersion を "34.0.0"に指定してください
5. build(Module:unityLibrary)の commandLineArgs.add("--tool-chain-path=" + android.ndkDirectry) の ndkDirectoryを、 ndkPathに修正してください
# Material-MultiSelection-Spinner
Material-MultiSelection-Spinner is a simple MultiSelection spinner which which is a wrapper of TextInputEditText. you can use with TextInputLayout, and build all application forms UI in same material Design.You can also set item click listener. and also manage callback on item selection as you want.


# template

![](https://github.com/zeeshan5422/Material-MultiSelection-Spinner/tree/master/app/demo/demo.gif)

## screenshots.
![](https://github.com/zeeshan5422/Material-MultiSelection-Spinner/tree/master/app/demo/img-1.jpeg)
![](https://github.com/zeeshan5422/Material-MultiSelection-Spinner/tree/master/app/demo/img-2.jpeg)
![](https://github.com/zeeshan5422/Material-MultiSelection-Spinner/tree/master/app/demo/img-3.jpeg)


## Gradle Dependency

Add this in your root build.gradle file (not your module build.gradle file):

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
Then, add the library to your module build.gradle

```
dependencies {
	        implementation 'com.github.zeeshan5422:Material-MultiSelection-Spinner:Tag'
	}
  ```
  
  ## Features

* Search bar (hide and show)
* unselect items button
* sorting the list
* Enable and disabled spinner
* change label color
* change selected item color in the list
* label text size
* label alignment (0 for left and 1 for center)
* selection with and without callback
* select multiple items, programmatically get list of selected items 
  
  
  
## Usage

There is a [sample](https://github.com/zeeshan5422/Material-MultiSelection-Spinner/tree/master/app/src/main) provided which shows how to use the library, but for completeness, here is all that is required to get Material Multi Seelction Spinner working:

### In xml layout,
```
<com.google.android.material.textfield.TextInputLayout
        style="@style/textInputLayoutStyle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:hint="Select Multiple Items">

        <com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner
            android:id="@+id/multi_Selection"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:clickable="true"
            android:focusableInTouchMode="false"
            android:importantForAutofill="no"
            android:maxLines="1"
            app:showSearch="true"
            app:sort="true"
            android:focusable="true" />

    </com.google.android.material.textfield.TextInputLayout>
    ```
    
    # In Code
    
    ```

        multiSelectionSpinner = findViewById(R.id.multi_Selection);
        multiSelectionSpinner.setItems(getItems());
        multiSelectionSpinner.setOnItemSelectedListener(new MultiSelectionSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, boolean isSelected, int position) {
            }

            @Override
            public void onSelectionCleared() {
            }
        });
    

    private List getItems() {
        ArrayList<String> alphabetsList = new ArrayList<>();
        for (char i = 'A'; i <= 'Z'; i++)
            alphabetsList.add(Character.toString(i));
        return alphabetsList;
    }
    
    ```
    
    
    
    That's it! :-)

if you are facing any kind of problem please let me know

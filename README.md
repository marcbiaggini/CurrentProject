        ____            _     ____                            ____   ___  ____  
	|  _ \ ___   ___| | __/ ___| _ __   ___   ___  _ __   |  _ \ / _ \/ ___| 
	| |_) / _ \ / __| |/ /\___ \| '_ \ / _ \ / _ \| '_ \  | |_) | | | \___ \ 
	|  _ < (_) | (__|   <  ___) | |_) | (_) | (_) | | | | |  __/| |_| |___) |
	|_| \_\___/ \___|_|\_\|____/| .__/ \___/ \___/|_| |_| |_|    \___/|____/ 
	                            |_|                                          
# API Reference for POS Project


### 3 - Clock In & Log In / Clock Out & Log Out

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Get Employee Open Clock Events| GET | /venue/{venueId}/employee/clock-events | [get_employee_open_clock_events](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/employee/get_employee_open_clock_events.md?at=master&fileviewer=file-view-default) |0|
| Employee Clock Action| POST      |   /venue/{venueId}/employee/{type} |[employee_clock_event](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/employee/employee_clock_event.md?at=master&fileviewer=file-view-default)|0|
| Device User Login | POST      |    /user/login |[device_user_login](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/device/device_user_login.md?at=master&fileviewer=file-view-default)|0|
| Forgot Password | GET     |    /user/forgot-password/search/{pattern} |[forgot_password](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/user/forgot_password.md?at=master&fileviewer=file-view-default)|0|

### 4 - Restaurant Layout

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Fetch Floor Plan| GET | /venue/dining-section | [get_venue_flor_plan](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/venue/layout/get_venue_flor_plan.md?at=master&fileviewer=file-view-default) |0|
| Create Dining Party| POST      |   /venue/ordering/diningparty/create |[create_dining_party](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/create_dining_party.md?at=master&fileviewer=file-view-default)|0|
| Update Dining Party | PUT      |    /venue/ordering/diningparty/update/{diningPartyId} |[update_dining_party](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_dining_party.md?at=master&fileviewer=file-view-default)|0|

### 4.2 - Restaurant Layout Merge Tables

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Update Dining Party| GET | /venue/ordering/diningparty/update/{diningPartyId} | [update_dining_party](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_dining_party.md?at=master&fileviewer=file-view-default) |0|

### 5 - Ordering

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Create Dinning Party| POST | /venue/ordering/diningparty/create | [create_dining_party](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/create_dining_party.md?at=master&fileviewer=file-view-default) |0|
| Fire Itens| POST | /venue/ordering/cart/{diningPartyId}/fire_items | [fire_itens](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/?at=master) |0|
| Get Dinning Party list by ID | POST | /venue/ordering/diningparty/list | [get_dining_party_list_by_ids](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/get_dining_party_list_by_ids.md?at=master&fileviewer=file-view-default) |0|
| Get Ordering Cart Itens List | GET | /venue/ordering/cart/{diningPartyId}/list_items | [get_ordering_cart_itens_list](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/get_ordering_cart_itens_list.md?at=master&fileviewer=file-view-default) |0|
| Get Ordering Dining Section Sport List by Ids  | POST | /venue/ordering/diningsectionspot/list | [get_ordering_dining_section_spot_list_by_ids](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/get_ordering_dining_section_spot_list_by_ids.md?at=master&fileviewer=file-view-default) |0|
| Get Ordering Tabs List by Id   | POST | /venue/ordering/tab/{diningPartyId}/list_entries | [get_ordering_tabs_list_by_id](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/get_ordering_tabs_list_by_id.md?at=master&fileviewer=file-view-default) |0|
| Update Dining Party   | PUT | /venue/ordering/diningparty/update/{diningPartyId} | [update_dining_party](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_dining_party.md?at=master&fileviewer=file-view-default) |0|
| Update Ordering Cart   | POST | /venue/ordering/cart/{diningPartyId}/update | [update_ordering_cart](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_ordering_cart.md?at=master&fileviewer=file-view-default) |0|
| Update Ordering Tab   | POST | /venue/ordering/tab/{diningPartyId}/update | [update_ordering_tab](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_ordering_tab.md?at=master&fileviewer=file-view-default) |0|
| Get Menus| GET      |   /venue/{venueId}/menu |[get_menus](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/menu/get_menus.md?at=master&fileviewer=file-view-default)|0|

### 7 - Closing / Tip Reconciliation

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Update Order Tab| POST | /venue/ordering/tab/{diningPartyId}/update | [update_ordering_tab](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/ordering/update_ordering_tab.md?at=master&fileviewer=file-view-default) |0|


### 8.1 - Main Menu / Profile

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Change User Short Password| PUT | /device/user/change-password | [change_user_device_short_password](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/device/change_user_device_short_password.md?at=master&fileviewer=file-view-default) |0|
| Update User Info| PUT      |   /device/user/profile  |Missing Documentation - More details at rockspoon-pos/android-sdk/services/UserService.java#update|0|


### 8.3 - Main Menu / Item Photos

| Resource        | Method  |Path | Description | Version |
| ------------- |:-------------:| :-----:| --- | ---- |
| Update Item Photo| PUT | /venue/{venueId}/item/{itemId}/photo | [update_item_photo](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/item/update_item_photo.md?at=master&fileviewer=file-view-default) |0|
| get_venue_itens| GET      |   /venue/{venueId}/item |[get_menus](https://bitbucket.org/rockspoon/rockspoon-docs/src/ff2325155b73ffffd75bb6efaa469aa851dca91a/Books/Backend/api/venue/get_venue_itens.md?at=master&fileviewer=file-view-default)|0|
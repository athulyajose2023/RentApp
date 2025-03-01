The Car Rental Booking App allows users to search for rental cars by specifying pickup and drop-off locations and dates. The app then redirects users to Kayak using deep links to complete their bookings.
Car Rental Search Screen

Users should be able to enter:

Pickup Location (City, State/Country) (Required)

Drop-off Location (Optional)

Pickup Date (YYYY-MM-DD format) (Required)

Drop-off Date (YYYY-MM-DD format)

A Search on Kayak button should generate the deep link and open Kayak.

2️⃣ Deep Linking to Kayak

Construct a Kayak deep link using the provided Affiliate Deep Link URL structure.

Format:

https://{KAYAK_Domain}/in?a={Affiliate_ID}&url=/cars/{Pick_Up_Location}/{Drop_Off_Location}/{Pick_Up_Date}/{Drop_Off_Date}

Example:

https://www.kayak.com/in?a=awesomecars&url=/cars/Los-Angeles,CA/SFO/2025-03-01/2025-03-05

The app should open this URL in a web browser.

3️⃣ UI Requirements

Use Jetpack Compose (preferred) or XML with RecyclerView for input fields.

Implement basic validation (e.g., Pickup Location & Date should be required).

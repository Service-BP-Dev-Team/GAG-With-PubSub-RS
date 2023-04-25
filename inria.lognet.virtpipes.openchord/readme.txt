Careful with the use of this bundle with keyvaluestore:
- if you set both open-chord and keyvaluestore, which is also configure with open-chord, you will do retrieves and inserts twice.
Prefer the use of KeyValueStore as it is a great abstraction of Key Value Storage.

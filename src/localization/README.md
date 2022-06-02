# Localization
If you want to add a localized string:
1. Add a new identifier in file *texts.msg* (symbol '>' in the beginning of the line marks an identifier)
2. Add localized strings that have to start with locale identifier ('ru'/'en')
3. Launch Python3 script *generat_l10n_java.py*. It'll recreate file *L10n.java* which will contains the new localized string
 

# The script parses file texts.msg (that is located in the folder in which this script is running)
# and creates L10n.java file that contains all localised texts

end_line = '\n'
comment_start = '#'
identifier_start = '>'
input_filename = 'texts.msg'
output_filename = 'L10n.java'

def prepare_line_to_parse(line):
	line = line.lstrip()
	if line.endswith(end_line):
		line = line[:-len(end_line)]
	return line

def is_comment(line):
    return line.startswith(comment_start)

def extract_id(line):
    if line.startswith(identifier_start):
        return line[1:].lstrip().upper()
    return ""
    
def extract_localised_string(line):
    space_pos = line.find(" ")
    if space_pos == -1:
        return line.lower(), ""
    return line[:space_pos].lower(), line[space_pos + 1:].lstrip()
   
def escape(string):
    return string.replace('"', '\\"')
   
# -----------------------------------------------------------------

# Read source file
translations = {}
with open(input_filename, 'r', encoding='utf-8') as file:
    lines = file.readlines()
    current_string_id = ''
    for line in lines:
        line = prepare_line_to_parse(line)
        if not line:
            current_string_id = ''
            continue
        if is_comment(line):
            continue
        string_id = extract_id(line)
        if string_id:
            current_string_id = string_id
            translations[current_string_id] = {}
            continue
        locale, string = extract_localised_string(line)
        translations[current_string_id][locale] = string

# Form content
lines = []
lines.append('// WARNING: This file was generated automatically (see generate_lang_java.py)')
lines.append('')
lines.append('/*')
lines.append(' * Copyright (C) 2022 The Java Open Source Project')
lines.append(' *')
lines.append(' * Licensed under the Apache License, Version 2.0 (the "License"); you may not')
lines.append(' * use this file except in compliance with the License. You may obtain a copy of')
lines.append(' * the License at')
lines.append(' *')
lines.append(' * http://www.apache.org/licenses/LICENSE-2.0')
lines.append(' *')
lines.append(' * Unless required by applicable law or agreed to in writing, software')
lines.append(' * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT')
lines.append(' * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the')
lines.append(' * License for the specific language governing permissions and limitations under')
lines.append(' * the License.')
lines.append(' */')
lines.append('')
lines.append('package localization;')
lines.append('')
lines.append('import java.text.MessageFormat;')
lines.append('')
lines.append('/**')
lines.append(' * The {@L10n} is class to get localized text resources.')
lines.append(' *')
lines.append(' * @author olegshchepilov')
lines.append(' *')
lines.append(' */')
lines.append('')
lines.append('public class L10n {')
lines.append('    public static String locale = "en";')
lines.append('    ')
lines.append('    public enum Id {')
last_string_id = list(translations)[-1]
for string_id in translations:
    lines.append('        ' + string_id + ('' if last_string_id == string_id else ','))
lines.append('    }')
lines.append('')
lines.append('    public static String get(Id stringId) {')
lines.append('        switch(stringId) {')
for string_id in translations:
    locale_dict = translations[string_id]
    lines.append('            case ' + string_id + ':')
    if len(locale_dict) == 0:
        lines.append('                return "{Missing:' + string_id + '}";')
    else:
        lines.append('                switch (locale) {')
        for locale in locale_dict:
            lines.append('                    case "' + locale + '": return "' + escape(locale_dict[locale]) + '";')
        lines.append('                    default: return "{Missing(" + locale + "):' + string_id + '}";') 
        lines.append('                }')
lines.append('            default: return "{Unknown}";')
lines.append('        }')
lines.append('    }')
lines.append('')
lines.append('    public static String get(Id stringId, Object... arguments) {')
lines.append('        return (new MessageFormat(get(stringId))).format(arguments);')
lines.append('    }')
lines.append('}')

# Write target file
lines = [(line + '\n') for line in lines]
with open(output_filename, 'w', encoding='utf-8') as file:
    file.writelines(lines)
 
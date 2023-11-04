import os
import xml.etree.ElementTree as ET

def count_smoked_occurrences(folder_path):
    total_occurrences = 0

    for filename in os.listdir(folder_path):
        if filename.endswith('.xml'):
            xml_path = os.path.join(folder_path, filename)
            try:
                tree = ET.parse(xml_path)
                root = tree.getroot()

                # Count occurrences of 'smoked' in the XML content
                occurrences_in_file = sum(1 for elem in root.iter() if 'smoked' in (elem.text or ''))
                total_occurrences += occurrences_in_file
            except ET.ParseError:
                print(f"Error parsing XML file: {filename}")

    return total_occurrences

if __name__ == "__main__":
    folder_path = "d:/desktop/temp/Annotations"  # Replace with your folder path
    total_occurrences = count_smoked_occurrences(folder_path)
    print(f"Total occurrences of 'smoked' in XML files: {total_occurrences}")

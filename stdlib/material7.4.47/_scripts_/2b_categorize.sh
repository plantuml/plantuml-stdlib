#!/usr/bin/env bash
set -e

to_upper_camel_case() {
  local input="$1"
  local result=""
  
  # Split by hyphen and capitalize first letter of each part
  IFS='-' read -ra PARTS <<< "$input"
  for part in "${PARTS[@]}"; do
    # Capitalize first letter of each part
    result+="$(tr '[:lower:]' '[:upper:]' <<< ${part:0:1})${part:1}"
  done
  
  echo "$result"
}

cd ../_intermediate_

echo "Categorizing icons by tags..."
jq '
  reduce .[] as $item ({}; 
    if ($item.tags | length) == 0 then
      .Miscellaneous += [$item.name]
    else
      reduce $item.tags[] as $tag (.; 
        .[$tag] += [$item.name]
      )
    end
  )
' meta.json > categorized.json

# Create a new JSON file with sanitized category keys
jq '
  with_entries(
    .key |= gsub("[^a-zA-Z0-9_-]"; "") | 
    .
  )
' categorized.json > categorized_sanitized.json

# Replace the original with the sanitized version
mv categorized_sanitized.json categorized.json

# TODO remove meta.json after categorization
# echo "Removing meta.json..."
# rm -f meta.json

echo "Organizing icons into category folders..."

# Create the base categorized directory
mkdir -p categorized

# stores all categories that they can be loaded in (over)all.puml
echo "[" > "categorized/all.json"

# Process each category in the JSON file using a different approach
jq -r 'keys[]' categorized.json | while read -r category; do
    echo "Processing category: $category"

    # Create the category directory
    mkdir -p "categorized/$category"

    # Get the count of icons in this category
    icon_count=$(jq -r ".\"$category\" | length" categorized.json)
    echo "    Number of icons: $icon_count"

    echo "  \"$category\"," >> "categorized/all.json"

    # Process each icon individually and collect them in all.puml too
    echo > "categorized/$category/all.puml"

    for i in $(seq 0 $((icon_count - 1))); do
        icon=$(jq -r ".\"$category\"[$i]" categorized.json)
        fileName=$(to_upper_camel_case "${icon}")
        if [ -f "converted/${icon}.puml" ]; then
            cp "converted/${icon}.puml" "categorized/$category/${fileName}.puml"
            echo "    Copied: ${fileName}.puml"

            cat "categorized/$category/${fileName}.puml" >> "categorized/$category/all.puml"
            echo >> "categorized/$category/all.puml"
        else
            echo "    Warning: ${icon}.puml not found in converted folder"
        fi
    done
done

echo "]" >> "categorized/all.json"
# Remove the last comma
sed -i '$!N;s/,\n]/\n]/' categorized/all.json


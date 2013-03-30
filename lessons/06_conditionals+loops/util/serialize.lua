-- Escape special charcodes so they can be embedded in string, e.g. newlines, backslashes, etc.
function espace_string(str)
	return str:gsub("\\", "\\\\"):gsub("\"", "\\\""):gsub("\r", "\\r"):gsub("\n", "\\n")
end

-- Return string with 'pretty' serialization of table
function serialize_table(t, indent)
	if indent == nil then
		indent = ""
	end

	local commaNewLine= "\n"
	local str = ""
	str = str.."{"
		for i,v in pairs(t) do
			local iStr=""
			if type(i)=="number" then
				iStr="["..i.."]"
			elseif type(i)=="string" then
				iStr="[\""..i.."\"]"
			end

			if type(v)=="number" then
				str = str..commaNewLine..indent.."\t"..iStr.." = "..v..""
			elseif type(v)=="string" then
				str = str..commaNewLine..indent.."\t"..iStr.." = \""..espace_string(v).."\""
			elseif type(v)=="table" then
				str = str..commaNewLine..indent.."\t"..iStr.." = "..serialize_table(v, indent.."\t")
			end
			commaNewLine= ",\n"
		end
	str = str.."}"
	return str
end


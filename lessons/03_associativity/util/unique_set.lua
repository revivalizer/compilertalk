-- Unique set
-- You can ask this class for the id of any element. Elements will be numbered from 1..n, the number of distinct elements that has been seens by this class.
-- This can be used to make sure that only unique references to constants or strings or tables are stored

unique_set_mt = {}

unique_set = {}
unique_set_mt = { __index = unique_set }

function create_unique_set()
	local set = {}
	setmetatable(set, unique_set_mt)

	set.values = {}
	set.count = 0

	return set
end

function unique_set:get_id(key)
	local id = self.values[key]

	if (id==nil) then
		self.count = self.count + 1
		id = self.count
		self.values[key] = id
	end

	return id
end

function unique_set:to_table()
	local t = {}

	for i,v in pairs(self.values) do
		t[v] = i
	end

	return t
end

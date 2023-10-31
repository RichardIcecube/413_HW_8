class DeltaDebugging:
    tests = list()

    def split_list(self, input_list: list) -> (list, list):
        midpoint = len(input_list) //2
        first_half = input_list[:midpoint]
        second_half = input_list[midpoint:]
        first_half.sort()
        second_half.sort()
        return first_half, second_half
    
    def union_lists(self, list1:list, list2:list):
        # Use a set store unique elements
        unique_elements = set()

        unique_elements.update(list1)
        unique_elements.update(list2)

        # Convert the set back to a list
        union_list = list(unique_elements)

        union_list.sort()

        return union_list
    
    def dd2(self, c: list, r: list, idx: int):
        if len(c) == 1:
            return c
        
        c1, c2 =self.split_list(c)
        if self.test(self.union_lists(c1, r), idx + 1) == TestOutcome.FAILURE:
            return self.dd2(c1, r, idx + 1)
        elif self.test(self.union_lists(c2, r), idx + 1) == TestOutcome.FAILURE:
            return self.dd2(c2, r, idx + 1)
        first = self.dd2(c1, self.union_lists(c2, r), idx + 1)
        second = self.dd2(c2, self.union_lists(c1, r), idx + 1)
        return self.union_lists(first, second)
    
    def dd(self, c: list) -> list:
        return self.dd2(c, list(), 0)
    
    def extract_number_from_patch_file(self, file_name) -> str:
        pattern = r'(\d{3})'
        match = re.search(pattern, file_name)
        if match:
            extracted_number = match.group(1)
            return extracted_number
        return ''
    
    if __name__ == '__main__':
        change_set = list()
        file_list = os.listdir("./changesets")

        # create ther initial change set
        for file in file_list:
            change_set.append(file)

        change_set.sort()
        delta_debugging = DeltaDebugging()
        res = delta_debugging.dd(change_set)

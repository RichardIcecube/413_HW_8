from enum import Enum
import os
import subprocess
import re


class TestOutcome(Enum):
    PASS = 'PASS'
    FAILURE = 'FAIL'
    UNRESOLVED = 'UNRESOLVED'


def run_shell(arg):
    return subprocess.run(arg, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


class DeltaDebugging:
    tests = list()

    def split_list(self, input_list: list) -> (list, list):
        midpoint = len(input_list) // 2
        first_half = input_list[:midpoint]
        second_half = input_list[midpoint:]
        first_half.sort()
        second_half.sort()
        return first_half, second_half

    def union_lists(self, list1: list, list2: list):
        # Use a set to store unique elements
        unique_elements = set()

        unique_elements.update(list1)
        unique_elements.update(list2)

        # Convert the set back to a list
        union_list = list(unique_elements)

        union_list.sort()

        return union_list

    def test(self, change_set: list, idx: int) -> TestOutcome:
        file_path = 'old/file1v1.java'

        if os.path.exists(file_path):
            os.remove(file_path)

        if os.path.exists('old/test_case/file1v1.class'):
            os.remove('old/test_case/file1v1.class')

        if os.path.exists('old/test_case'):
            os.rmdir('old/test_case')

        run_shell('cp old/file1v1_orig.java old/file1v1.java')

        for c in change_set:
            run_shell('patch -p0 -i changesets/{} < old/file1v1.java'.format(c))

        run_shell('javac old/file1v1.java && mkdir old/test_case && mv old/file1v1.class old/test_case/file1v1.class')
        p = run_shell('cd old && java test_case/file1v1 5 0 division')

        self.tests.append({'change_set': change_set, 'status': p.returncode})

        if p.returncode == 1:
            return TestOutcome.FAILURE
        elif p.returncode == 0:
            return TestOutcome.PASS
        else:
            return TestOutcome.UNRESOLVED

    def dd2(self, c: list, r: list, idx: int):
        if len(c) == 1:
            return c

        c1, c2 = self.split_list(c)
        if self.test(self.union_lists(c1, r), idx + 1) == TestOutcome.FAILURE:
            return self.dd2(c1, r, idx + 1)
        if self.test(self.union_lists(c2, r), idx + 1) == TestOutcome.FAILURE:
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

    def print_results(self, cs: list):
        print('Delta-debugging Project')
        for c in cs:
            file_path = 'changesets/{}'.format(c)
            try:
                with open(file_path, 'r') as f:
                    content = f.read()
                    print(content)
            except FileNotFoundError:
                print(f"File not found: {file_path}")
            except Exception as e:
                print(f"An error occurred: {e}")

        print('# of Total Change sets is = {}'.format(len(cs)))

        idx = 1
        for test in self.tests:
            change_set_list = list()
            for c in test['change_set']:
                extracted_number = self.extract_number_from_patch_file(c)
                change_set_list.append(extracted_number)

            status = 'PASS'
            if test['status'] == 1:
                status = 'FAIL'
            print('Step {}: c_{}: {} {}'.format(idx, idx, ' '.join(change_set_list), status))
            idx += 1


if __name__ == '__main__':
    change_set = list()

    file_list = os.listdir("./changesets")

    # create the initial change set
    for file in file_list:
        change_set.append(file)

    change_set.sort()

    delta_debugging = DeltaDebugging()
    res = delta_debugging.dd(change_set)

    delta_debugging.print_results(change_set)

    print('Changes where bugs occurred: [{}]'.format(delta_debugging.extract_number_from_patch_file(res[0])))












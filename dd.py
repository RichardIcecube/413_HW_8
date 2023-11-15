import subprocess
import os

tests = list()

def run_command(command):
    return subprocess.run(command, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

def union(first: list, second: list):
    unique = set()
    
    unique.update(first)
    print("Union First: {}".format(list(unique)))
    unique.update(second)
    print("Union Second: {}".format(list(unique)))
    
    return list(unique)

#I believe Quang actually wrote this code, so I will leave it relatively unchanged
def test(change_set: list):

    print("Test: {}".format(change_set))
    if os.path.exists('firstv/file1v1.java'):
        os.remove('firstv/file1v1.java')

    if os.path.exists('firstv/test_case/file1v1.class'):
        os.remove('firstv/test_case/file1v1.class')

    if os.path.exists('firstv/test_case'):
        os.rmdir('firstv/test_case')

    run_command('cp firstv/original.java firstv/file1v1.java')

    if change_set is not None: 
        for c in change_set:
            run_command('patch -p0 -i changefiles/{} < firstv/file1v1.java'.format(c))

    run_command('javac firstv/file1v1.java && mkdir firstv/test_case && mv firstv/file1v1.class firstv/test_case/file1v1.class')
    p = run_command('cd firstv && java test_case/file1v1 5 0 division')
    
    tests.append({'change_set': change_set, 'status': p.returncode})

    if p.returncode == 0:
        return 0 #Pass
    elif p.returncode == 1:
        return 1 #Fail
    else: 
        return 2 #Unresolved
    

def ddrecursive(changes: list, recursive: list):
    #returns if change list contains only one change
    if len(changes) == 1: 
        return changes
    
    #split array in half as specified by algorithm
    middle = len(changes) // 2
    split1 = changes[:middle]
    split1.sort()
    split2 = changes[middle:]
    split2.sort()
    
    #test each half of changes for buggs
    print(split1)
    print(split2)
    test1 = test(union(split1, recursive))
    if test1 == 1:
        return ddrecursive(split1, recursive)
    test2 = test(union(split2, recursive))
    if test2 == 1:
        return ddrecursive(split2, recursive)
    
    #No failures in each half, requires further subdivision
    front = ddrecursive(split1, union(split2, recursive))
    back = ddrecursive(split2, union(split1, recursive))
    return union(front, back)
    
if __name__ == '__main__':
    changes = list()
    
    changelist = os.listdir("./changefiles")
    
    for change in changelist:
        changes.append(change)
    
    results = ddrecursive(changes, list())
    
    #I believe Quang actually wrote this portion of the code as well, so I will be repurposing it 
    for change in changes:
        directory = 'changefiles/{}'.format(change)
        try:
            with open(directory, 'r') as f:
                file_contents = f.read()
                print(file_contents)
        except FileNotFoundError:
            print(f"File not found: {directory}")
        except Exception as e:
            print(f"An error occurred: {e}")
    print('# of Total Change sets is = {}'.format(len(changes)))
    #This portion I also believe Quang wrote, so I will use it as well
    idx = 1
    for test in tests:
        change_set_list = list()
        if test['change_set'] is not None:
            for c in test['change_set']:
                change_set_list.append(c[:3])

        status = 'PASS'
        if test['status'] == 1:
            status = 'FAIL'
        print('Step {}: c_{}: {} {}'.format(idx, idx, ' '.join(change_set_list), status))
        idx += 1
    
    print('Changes where bugs occurred: [{}]'.format(results[0]))

    

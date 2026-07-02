package com.telco.members.tcos.service;
import com.telco.members.tcos.dto.MembersRegistrationDTO;
import com.telco.members.tcos.model.Members;
import com.telco.members.tcos.repository.usersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.telco.members.tcos.repository.MembersRepository;

@Service
public class MembersService {

    @Autowired
    private usersRepository Usersrepo;

	@Autowired
    private MembersRepository MembersRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(MembersRegistrationDTO MembersDTO) {
        Members members = new Members();
        members.setMid(MembersDTO.getMid());
        members.setEmail(MembersDTO.getEmail());
        members.setFirstName(MembersDTO.getFirstName());
        members.setLastName(MembersDTO.getLastName());

        members.setPassword(bCryptPasswordEncoder.encode(MembersDTO.getPassword()));

        MembersRepository.save(members);
    }

    public boolean checkPassword(String mid, String rawPassword) {
        Members members = (Members) MembersRepository.findBymid(mid);

        if(members!=null && bCryptPasswordEncoder.matches(rawPassword, members.getPassword()))
        	return true;

        return false;
    }

    public Members findByMid(String mid) {
        return MembersRepository.findBymid(mid);
    }

    public int countActive(){
        int value = Usersrepo.getActiveUsers();
        return value;
    }

    public int countInActive(){
        int value = Usersrepo.getInActiveUsers();
        return value;
    }

    public String findEmailId(String employeeId) {
        return MembersRepository.findEmailIdByMid(employeeId);
    }

    public void updatePassword(String employeeId, String newPassword) {
        String pass = bCryptPasswordEncoder.encode(newPassword);
        MembersRepository.updatePasswordForEmpID(employeeId, pass);
    }
}

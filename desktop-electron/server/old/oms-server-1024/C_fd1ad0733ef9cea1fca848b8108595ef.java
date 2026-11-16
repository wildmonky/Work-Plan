import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_fd1ad0733ef9cea1fca848b8108595ef
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("TZNR").toString();
sResult=String.format("<span onclick=\"viewPwElinkInfoHis(this)\">查看</span>",sResult);
return sResult;
    }
}

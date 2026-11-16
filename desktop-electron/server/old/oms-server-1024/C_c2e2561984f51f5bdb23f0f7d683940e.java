import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_c2e2561984f51f5bdb23f0f7d683940e
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("NOTIFY_CONTENT").toString();
sResult=String.format("<span onclick=\"viewPwElinkInfoHis(this)\">查看</span>",sResult);
return sResult;
    }
}

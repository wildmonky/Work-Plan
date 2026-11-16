import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_e3c1eb28cd86059d8f8c07413c40be3b
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=(mapRow.get("ACTIVED").equals("1"))?"<font color=blue>可用</font>":"<font color=red>禁用</font>";return sResult;
    }
}
